package red.man10.velocity.manager.config.sub

import com.velocitypowered.api.proxy.server.ServerInfo
import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig
import java.net.InetSocketAddress

class ServerConfig: AbstractConfig() {
    override val internalName: String = "server"

    var login = "login"
    var man10 = "man10"
    var jail = "jail"

    val servers = ArrayList<ServerInfo>()

    override fun loadConfig(config: CommentedConfigurationNode) {
        login = config.node("login").getString(login)
        man10 = config.node("man10").getString(man10)
        jail = config.node("jail").getString(jail)

        servers.clear()
        val serversMap = config.node("servers").childrenMap()
        serversMap.forEach { (k, v) ->
            val key = k.toString()
            val address = v.node("address").string ?: return@forEach
            val port = v.node("port").getInt(-1)
            if (port == -1) return@forEach
            servers.add(ServerInfo(key, InetSocketAddress(address, port)))
        }
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("login").set("login")
        config.node("man10").set("man10")
        config.node("jail").set("jail")
        config.node("servers").set(mapOf<String, Any>())
    }
}