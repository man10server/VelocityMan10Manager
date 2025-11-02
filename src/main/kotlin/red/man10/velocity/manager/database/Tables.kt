package red.man10.velocity.manager.database

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import red.man10.velocity.manager.database.models.BanIP
import red.man10.velocity.manager.database.models.CommandLog
import red.man10.velocity.manager.database.models.ConnectionLog
import red.man10.velocity.manager.database.models.MessageLog
import red.man10.velocity.manager.database.models.PlayerData
import red.man10.velocity.manager.database.models.ScoreLog
import java.util.Optional
import java.util.UUID

class PlayerDataTable(tableName: String): Table<PlayerData>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").transform({ UUID.fromString(it) }, { it.toString() }).bindTo { it.uuid }
    val player = varchar("player").bindTo { it.player }
    val freezeUntil = datetime("freeze_until").bindTo { it.freezeUntil }
    val muteUntil = datetime("mute_until").bindTo { it.muteUntil }
    val jailUntil = datetime("jail_until").bindTo { it.jailUntil }
    val banUntil = datetime("ban_until").bindTo { it.banUntil }
    val banMessageOverride = varchar(name = "ban_message_override")
        .transform({ s: String? -> Optional.ofNullable(s) }, { opt: Optional<String> -> opt.orElse(null) })
        .bindTo { it.banMessageOverride }
    val msbUntil = datetime("msb_until").bindTo { it.msbUntil }
    val score = int("score").bindTo { it.score }
}

class BanIPTable(tableName: String): Table<BanIP>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val ipAddress = varchar("ip_address").bindTo { it.ipAddress }
    val date = datetime("date").bindTo { it.date }
    val reason = varchar("reason").bindTo { it.reason }
}

class ScoreLogTable(tableName: String): Table<ScoreLog>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").transform({ UUID.fromString(it) }, { it.toString() }).bindTo { it.uuid }
    val player = varchar("player").bindTo { it.player }
    val score = int("score").bindTo { it.score }
    val note = varchar("note").bindTo { it.note }
    val issuer = varchar("issuer").bindTo { it.issuer }
    val nowScore = int("now_score").bindTo { it.nowScore }
    val date = datetime("date").bindTo { it.date }
}

class ConnectionLogTable(tableName: String): Table<ConnectionLog>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").transform({ UUID.fromString(it) }, { it.toString() }).bindTo { it.uuid }
    val player = varchar("player").bindTo { it.player }
    val server = varchar("server").bindTo { it.server }
    val connectedTime = datetime("connected_time").bindTo { it.connectedTime }
    val disconnectedTime = datetime("disconnected_time").bindTo { it.disconnectedTime }
    val connectionSeconds = int("connection_seconds").bindTo { it.connectionSeconds }
    val ip = varchar("ip").bindTo { it.ip }
    val port = int("port").bindTo { it.port }
}

class CommandLogTable(tableName: String): Table<CommandLog>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").transform({ UUID.fromString(it) }, { it.toString() }).bindTo { it.uuid }
    val player = varchar("player").bindTo { it.player }
    val command = varchar("command").bindTo { it.command }
    val server = varchar("server").bindTo { it.server }
    val date = datetime("date").bindTo { it.date }
}

class MessageLogTable(tableName: String): Table<MessageLog>(tableName) {
    val id = int("id").primaryKey().bindTo { it.id }
    val uuid = varchar("uuid").transform({ UUID.fromString(it) }, { it.toString() }).bindTo { it.uuid }
    val player = varchar("player").bindTo { it.player }
    val message = varchar("message").bindTo { it.message }
    val server = varchar("server").bindTo { it.server }
    val date = datetime("date").bindTo { it.date }
}