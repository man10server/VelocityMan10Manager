package red.man10.velocity.manager

import com.github.ucchyocean.lc.japanize.JapanizeType
import com.github.ucchyocean.lc.japanize.Japanizer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger
import red.man10.velocity.manager.command.CommandRegister
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.GeneralConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.ServerConfig
import red.man10.velocity.manager.database.Database
import red.man10.velocity.manager.discord.DiscordBot
import red.man10.velocity.manager.listeners.PlayerListener
import java.nio.file.Path


@Plugin(
    id = "velocityman10manager",
    name = "VelocityMan10Manager",
    version = "1.0-SNAPSHOT",
    description = "A Velocity plugin for managing Man10 servers",
    authors = ["tororo_1066"]
)
class VelocityMan10Manager {

    companion object {
        lateinit var proxy: ProxyServer
        lateinit var dataDirectory: Path
        lateinit var logger: Logger

        val japanizerDictionary = HashMap<String, String>()

        val registeredServers = ArrayList<ServerInfo>()

        fun sendMessageToMinecraftPlayers(component: Component) {
            proxy.allPlayers.forEach { player ->
                player.sendMessage(component)
            }
        }

        fun sendMessageToMinecraftPlayers(message: String) {
            sendMessageToMinecraftPlayers(MiniMessage.miniMessage().deserialize(message))
        }

        fun log(message: String) {
            val config = Config.getOrThrow<GeneralConfig>()
            logger.info("${config.prefix}$message")
            DiscordBot.admin(message)
        }

        fun warning(message: String) {
            val config = Config.getOrThrow<GeneralConfig>()
            logger.warn("${config.prefix}$message")
            DiscordBot.admin("[Warning]$message")
        }

        fun error(message: String) {
            val config = Config.getOrThrow<GeneralConfig>()
            logger.error("${config.prefix}$message")
            DiscordBot.admin("[Error]$message")
        }

        fun japanize(message: String): String? {
            return Japanizer.japanize(
                message,
                JapanizeType.GOOGLE_IME,
                japanizerDictionary
            ).ifEmpty { null }
        }

        fun reloadAll() {
            Config.load()
            reloadServers()
            Database.reloadDatabase()
            DiscordBot.reload()
        }

        fun reloadServers() {
            val serverConfig = Config.getOrThrow<ServerConfig>()
            registeredServers.forEach {
                proxy.unregisterServer(it)
            }
            registeredServers.clear()
            serverConfig.servers.forEach {
                proxy.registerServer(it)
                registeredServers.add(it)
            }
        }

        fun miniMessage(message: String): Component {
            return MiniMessage.miniMessage().deserialize(message)
        }
    }

    @Inject
    constructor(proxy: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) {
        Companion.proxy = proxy
        Companion.logger = logger
        Companion.dataDirectory = dataDirectory

        Config.load()
    }

    @Subscribe
    fun onProxyInitialize(e: ProxyInitializeEvent) {
        CommandRegister.register(proxy)
        try {
            Database
        } catch (ex: Throwable) {
            error("Failed to initialize database: ${ex.message}")
            error("Shutting down the proxy...")
            ex.printStackTrace()
            proxy.shutdown()
            return
        }
        try {
            DiscordBot
        } catch (ex: Throwable) {
            error("Failed to initialize discord bot: ${ex.message}")
            ex.printStackTrace()
        }

        val messageConfig = Config.getOrThrow<MessageConfig>()
        DiscordBot.chat(messageConfig.serverBootMessage)

        proxy.eventManager.register(this, PlayerListener())

        reloadServers()
    }

    @Subscribe
    fun onProxyShutdown(e: ProxyShutdownEvent) {
        val messageConfig = Config.getOrThrow<MessageConfig>()
        DiscordBot.chatChannel?.sendMessage(messageConfig.serverShutdownMessage)?.complete()
        DiscordBot.jda?.shutdown()
    }
}