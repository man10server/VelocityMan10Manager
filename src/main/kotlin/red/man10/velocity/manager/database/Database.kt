package red.man10.velocity.manager.database

import com.velocitypowered.api.proxy.Player
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.asc
import org.ktorm.dsl.count
import org.ktorm.dsl.eq
import org.ktorm.dsl.forEach
import org.ktorm.dsl.from
import org.ktorm.dsl.groupBy
import org.ktorm.dsl.inList
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.like
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.*
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.DatabaseConfig
import red.man10.velocity.manager.database.models.BanIP
import red.man10.velocity.manager.database.models.CommandLog
import red.man10.velocity.manager.database.models.ConnectionLog
import red.man10.velocity.manager.database.models.MessageLog
import red.man10.velocity.manager.database.models.PlayerData
import red.man10.velocity.manager.database.models.ScoreLog
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object Database {

    lateinit var database: Database

    lateinit var playerDataTable: PlayerDataTable
    lateinit var banIPTable: BanIPTable
    lateinit var scoreLogTable: ScoreLogTable
    lateinit var connectionLogTable: ConnectionLogTable
    lateinit var commandLogTable: CommandLogTable
    lateinit var messageLogTable: MessageLogTable

    val playerData get() = database.sequenceOf(playerDataTable)
    val banIP get() = database.sequenceOf(banIPTable)
    val scoreLog get() = database.sequenceOf(scoreLogTable)
    val connectionLog get() = database.sequenceOf(connectionLogTable)
    val commandLog get() = database.sequenceOf(commandLogTable)
    val messageLog get() = database.sequenceOf(messageLogTable)

    val playerDataCache = ConcurrentHashMap<UUID, PlayerData>()
    var banIPCache = ArrayList<String>()


    val connectedTime = HashMap<Pair<UUID, String>, Pair<LocalDateTime, Int>>()

    fun createDatabase(config: DatabaseConfig): Database {
        return Database.connect(
            url = "jdbc:mysql://${config.host}:${config.port}/${config.database}${config.options}",
            driver = "com.mysql.cj.jdbc.Driver",
            user = config.username,
            password = config.password
        )
    }

    init {
        reloadDatabase()
        updateBanIPCache()
    }

    fun reloadDatabase() {
        val config = Config.getOrThrow<DatabaseConfig>()
        database = createDatabase(config)
        playerDataTable = PlayerDataTable(config.playerDataTable)
        banIPTable = BanIPTable(config.banIpTable)
        scoreLogTable = ScoreLogTable(config.scoreLogTable)
        connectionLogTable = ConnectionLogTable(config.connectionLogTable)
        messageLogTable = MessageLogTable(config.messageLogTable)
        commandLogTable = CommandLogTable(config.commandLogTable)
    }

    fun getPlayerDataByName(name: String): PlayerData? {
        val data = playerData.find { it.player eq name } ?: return null
        return data
    }

    fun updateBanIPCache() {
        val newCache = ArrayList<String>()
        banIP.forEach {
            newCache.add(it.ipAddress)
        }
        banIPCache = newCache
    }

    fun addBanIP(ip: String, reason: String) {
        val log = BanIP {
            this.ipAddress = ip
            this.reason = reason
            this.date = LocalDateTime.now()
        }
        banIP.add(log)
        updateBanIPCache()
    }

    fun removeBanIP(ip: String): Boolean {
        val log = banIP.find { it.ipAddress eq ip } ?: return false
        log.delete()
        updateBanIPCache()
        return true
    }

    fun getJailedReason(uuid: UUID): String? {
        val log = scoreLog
            .filter { it.uuid eq uuid and (it.note like "%Jail") }
            .sortedByDescending { it.id }
            .firstOrNull()?.note ?: return null

        val reason = log
            .replace("[give]:", "")
            .replace("によりJail", "")

        return reason
    }

    fun addCommandLog(
        player: Player,
        command: String,
        server: String
    ) {
        val log = CommandLog {
            this.uuid = player.uniqueId
            this.player = player.username
            this.command = command
            this.server = server
            this.date = LocalDateTime.now()
        }
        commandLog.add(log)
    }

    fun addMessageLog(
        player: Player,
        message: String,
        server: String
    ) {
        val log = MessageLog {
            this.uuid = player.uniqueId
            this.player = player.username
            this.message = message
            this.server = server
            this.date = LocalDateTime.now()
        }
        messageLog.add(log)
    }

    fun connectedServer(player: Player, server: String) {
        val now = LocalDateTime.now()

        val log = ConnectionLog {
            this.uuid = player.uniqueId
            this.player = player.username
            this.server = server
            this.connectedTime = now
            this.ip = player.remoteAddress.address.hostAddress
            this.port = player.remoteAddress.port
        }

        connectionLog.add(log)

        connectedTime[Pair(player.uniqueId, server)] = Pair(now, log.id)
    }

    fun disconnectedServer(player: Player, server: String) {
        val now = LocalDateTime.now()
        val key = Pair(player.uniqueId, server)
        val connected = connectedTime[key] ?: return

        val duration = Duration.between(connected.first, now).seconds

        val log = connectionLog.find { it.id eq connected.second } ?: return
        log.disconnectedTime = now
        log.connectionSeconds = duration.toInt()
        log.flushChanges()

        connectedTime.remove(key)
    }

    fun giveScore(
        playerUUID: UUID,
        score: Int,
        note: String
    ) {
        val data = playerData.find { it.uuid eq playerUUID } ?: return
        data.score += score
        data.flushChanges()

        val log = ScoreLog {
            this.uuid = data.uuid
            this.player = data.player
            this.score = score
            this.note = note
            this.date = LocalDateTime.now()
        }
        scoreLog.add(log)
    }

    data class SubAccountInfo(
        val playerData: PlayerData,
        val ip: String,
        val connectionCount: Int
    )

    fun getSubAccounts(name: String): List<SubAccountInfo> {
        val list = ArrayList<SubAccountInfo>()
        val source = database.from(connectionLogTable)
        val query = source
            .innerJoin(playerDataTable, on = connectionLogTable.uuid eq playerDataTable.uuid)
            .select(
                count().aliased("cnt"),
                playerDataTable.player,
                playerDataTable.uuid,
                connectionLogTable.ip,
                playerDataTable.id,
                playerDataTable.freezeUntil,
                playerDataTable.muteUntil,
                playerDataTable.jailUntil,
                playerDataTable.banUntil,
                playerDataTable.msbUntil,
                playerDataTable.score
            )
            .where {
                connectionLogTable.ip inList source
                    .select(connectionLogTable.ip)
                    .where {
                        connectionLogTable.player eq name
                    }
//                    .groupBy(connectionLogTable.player)
//                    .orderBy(connectionLogTable.ip.asc())
            }
            .groupBy(
                playerDataTable.player,
                playerDataTable.uuid,
                connectionLogTable.ip
            )

        query.forEach {
            val data = playerDataTable.createEntity(it)
            val count = it.getInt("cnt")
            val ip = it[connectionLogTable.ip] ?: return@forEach
            list.add(SubAccountInfo(data, ip, count))
        }

        return list
    }

}




