package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class DatabaseConfig: AbstractConfig() {
    override val internalName: String = "database"

    var host: String = "localhost"
    var port: Int = 3306
    var username: String = "root"
    var password: String = ""
    var database: String = "velocity"

    var playerDataTable = "player_data"
    var banIpTable = "ban_ip_list"
    var scoreLogTable = "score_log"
    var connectionLogTable = "connection_log"
    var messageLogTable = "message_log"
    var commandLogTable = "command_log"

    override fun loadConfig(config: CommentedConfigurationNode) {
        val mysql = config.node("mysql")
        host = mysql.node("host").getString("localhost")
        port = mysql.node("port").getInt(3306)
        username = mysql.node("username").getString("root")
        password = mysql.node("password").getString("")
        database = mysql.node("database").getString("velocity")
        val tables = config.node("tables")
        playerDataTable = tables.node("playerData").getString("player_data")
        banIpTable = tables.node("banIp").getString("ban_ip_list")
        scoreLogTable = tables.node("scoreLog").getString("score_log")
        connectionLogTable = tables.node("connectionLog").getString("connection_log")
        messageLogTable = tables.node("messageLog").getString("message_log")
        commandLogTable = tables.node("commandLog").getString("command_log")
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        val mysql = config.node("mysql")
        mysql.node("host").set("localhost")
        mysql.node("port").set(3306)
        mysql.node("username").set("root")
        mysql.node("password").set("")
        mysql.node("database").set("velocity")
        val tables = config.node("tables")
        tables.node("playerData").set("player_data")
        tables.node("banIp").set("ban_ip_list")
        tables.node("scoreLog").set("score_log")
        tables.node("connectionLog").set("connection_log")
        tables.node("messageLog").set("message_log")
        tables.node("commandLog").set("command_log")
    }
}