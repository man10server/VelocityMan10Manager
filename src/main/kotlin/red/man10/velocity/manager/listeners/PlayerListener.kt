package red.man10.velocity.manager.listeners

import com.velocitypowered.api.event.Continuation
import com.velocitypowered.api.event.EventTask
import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.count
import org.ktorm.entity.find
import red.man10.velocity.manager.AuthProvider
import red.man10.velocity.manager.Utils.getServerName
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.ChatConfig
import red.man10.velocity.manager.config.sub.GeneralConfig
import red.man10.velocity.manager.config.sub.LogConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.ServerConfig
import red.man10.velocity.manager.database.Database
import red.man10.velocity.manager.database.models.PlayerData
import red.man10.velocity.manager.discord.DiscordBot
import java.util.concurrent.CompletableFuture

class PlayerListener {

    @Subscribe
    fun onPlayerLogin(e: LoginEvent, continuation: Continuation) {
        val player = e.player
        val config = Config.getOrThrow<MessageConfig>()

        CompletableFuture.runAsync {
            val data = try {
                Database.playerData.find { it.uuid eq player.uniqueId }
            } catch (ex: Throwable) {
                VelocityMan10Manager.error("Error fetching player data for ${player.username}: ${ex.message}")
                ex.printStackTrace()
                null
            }
            if (data == null) {
                e.result = ResultedEvent.ComponentResult.denied(
                    VelocityMan10Manager.miniMessage(config.failedToConnectServerMessage)
                )
                return@runAsync
            }

            if (data.isMSB()) {
                e.result = ResultedEvent.ComponentResult.denied(
                    VelocityMan10Manager.miniMessage(config.msbMessage)
                )
                return@runAsync
            }

            if (data.isBanned()) {
                e.result = ResultedEvent.ComponentResult.denied(
                    VelocityMan10Manager.miniMessage(data.banMessageOverride ?: config.banMessage)
                )
                return@runAsync
            }

            if (Database.banIPCache.contains(player.remoteAddress.address.hostAddress)) {
                e.result = ResultedEvent.ComponentResult.denied(
                    VelocityMan10Manager.miniMessage(config.banMessage)
                )
                return@runAsync
            }

            Database.playerDataCache[player.uniqueId] = data
        }.whenComplete { _, _ ->
            continuation.resume()
        }

    }

    @Subscribe
    fun onPlayerJoin(e: PostLoginEvent) {
        val player = e.player
        val data = Database.playerDataCache[player.uniqueId]
        val config = Config.getOrThrow<MessageConfig>()
        val logConfig = Config.getOrThrow<LogConfig>()

        val score = data?.score ?: 0

        val loginMessage = config.minecraftLoginMessages.entries.find {
            score in it.key
        }?.value ?: "<dark_red>エラー メッセージ未定義"

        val nameScore = mapOf(
            "name" to player.username,
            "score" to score.toString()
        )

        VelocityMan10Manager.sendMessageToMinecraftPlayers(
            VelocityMan10Manager.miniMessage(
                loginMessage.applyPlaceholders(nameScore)
            )
        )
        if (logConfig.login) {
            DiscordBot.admin(
                logConfig.loginLogFormat.applyPlaceholders(nameScore)
            )
        }
        DiscordBot.chat(
            config.discordLoginMessage.applyPlaceholders(nameScore)
        )

        if (data == null) {
            AuthProvider.showAuthenticationMessage(player)
            return
        }

        if (data.isJailed()) {
            val serverConfig = Config.getOrThrow<ServerConfig>()
            val reason = Database.getJailedReason(data.uuid) ?: "不明"
            val reasonMap = mapOf("reason" to reason)
            player.sendMessage(
                VelocityMan10Manager.miniMessage(
                    config.jailMessage.applyPlaceholders(reasonMap)
                )
            )
            player.showTitle(
                Title.title(
                    VelocityMan10Manager.miniMessage(config.jailTitle.applyPlaceholders(reasonMap)),
                    VelocityMan10Manager.miniMessage(config.jailSubtitle.applyPlaceholders(reasonMap))
                )
            )
            val currentServer = player.currentServer.orElse(null)?.serverInfo?.name
            if (currentServer != serverConfig.jail) {
                val server = VelocityMan10Manager.proxy.getServer(serverConfig.jail)
                if (server.isPresent) {
                    player.createConnectionRequest(server.get()).fireAndForget()
                }
            }
            return
        }
    }

    @Subscribe
    fun onPlayerDisconnect(e: DisconnectEvent) {
        val player = e.player
        val data = Database.playerDataCache[player.uniqueId]
        val config = Config.getOrThrow<MessageConfig>()
        val logConfig = Config.getOrThrow<LogConfig>()
        val score = data?.score ?: 0

        val logoutMessage = config.minecraftLogoutMessages.entries.find {
            score in it.key
        }?.value ?: "<dark_red>エラー メッセージ未定義"

        val nameScore = mapOf(
            "name" to player.username,
            "score" to score.toString()
        )

        VelocityMan10Manager.sendMessageToMinecraftPlayers(
            VelocityMan10Manager.miniMessage(
                logoutMessage.applyPlaceholders(nameScore)
            )
        )
        if (logConfig.logout) {
            DiscordBot.admin(
                logConfig.logoutLogFormat.applyPlaceholders(nameScore)
            )
        }
        DiscordBot.chat(
            config.discordLogoutMessage.applyPlaceholders(nameScore)
        )

        Database.playerDataCache.remove(player.uniqueId)
    }

    @Subscribe
    fun onPlayerConnectOtherServer(e: ServerPreConnectEvent) {

        val serverConfig = Config.getOrThrow<ServerConfig>()
        val login = VelocityMan10Manager.proxy.getServer(serverConfig.login).orElse(null)

        val player = e.player
        val target = e.originalServer
        val previous = e.previousServer

        val data = Database.playerDataCache[player.uniqueId]

        if (data == null) {
            if (login != null) {
                e.result = ServerPreConnectEvent.ServerResult.allowed(login)
            } else {
                e.result = ServerPreConnectEvent.ServerResult.denied()
            }

            AuthProvider.showAuthenticationMessage(player)
            return
        }

        if (data.isJailed()) {
            val jail = VelocityMan10Manager.proxy.getServer(serverConfig.jail).orElse(null)
            if (jail != null && target != jail) {
                e.result = ServerPreConnectEvent.ServerResult.allowed(jail)
            } else if (jail == null) {
                e.result = ServerPreConnectEvent.ServerResult.denied()
            }
        }

        val server = e.result.server.orElse(null)?.serverInfo?.name ?: target.serverInfo.name
        CompletableFuture.runAsync {
            if (previous != null) {
                val prevName = previous.serverInfo.name
                Database.disconnectedServer(player, prevName)
            }

            Database.connectedServer(player, server)
        }
    }

    @Suppress("DEPRECATION")
    @Subscribe
    fun onPlayerChat(e: PlayerChatEvent): EventTask? {
        val generalConfig = Config.getOrThrow<GeneralConfig>()
        val messageConfig = Config.getOrThrow<MessageConfig>()
        val serverConfig = Config.getOrThrow<ServerConfig>()
        val chatConfig = Config.getOrThrow<ChatConfig>()
        val logConfig = Config.getOrThrow<LogConfig>()

        val player = e.player
        var message = e.message
        val data = Database.playerDataCache[player.uniqueId]

        if (data == null) {
            e.result = PlayerChatEvent.ChatResult.denied()
            if (!AuthProvider.verifyCode(player, message)) {
                AuthProvider.showAuthenticationMessage(player)
                return null
            }

            player.sendMessage(
                VelocityMan10Manager.miniMessage(
                    messageConfig.authenticationSuccessMessage
                )
            )

            return EventTask.async {

                try {
                    val playerData = PlayerData {
                        this.uuid = player.uniqueId
                        this.player = player.username
                    }
                    Database.playerData.add(playerData)
                    Database.playerDataCache[player.uniqueId] = playerData

                    val count = Database.playerData.count()

                    Thread.sleep(5000)

                    val nameCount = mapOf(
                        "name" to player.username,
                        "count" to count.toString()
                    )

                    VelocityMan10Manager.sendMessageToMinecraftPlayers(
                        VelocityMan10Manager.miniMessage(
                            messageConfig.minecraftFirstLoginMessage.applyPlaceholders(nameCount)
                        )
                    )

                    DiscordBot.chat(
                        messageConfig.discordFirstLoginMessage.applyPlaceholders(nameCount)
                    )

                    val currentServer = player.currentServer.orElse(null)?.serverInfo?.name
                    if (currentServer != serverConfig.man10) {
                        val server = VelocityMan10Manager.proxy.getServer(serverConfig.man10)
                        if (server.isPresent) {
                            player.createConnectionRequest(server.get()).fireAndForget()
                        }
                    }
                } catch (e: Throwable) {
                    player.disconnect(Component.text("§cエラーが発生しました"))
                    VelocityMan10Manager.error("Error onPlayerChat: ${e.message}")
                    e.printStackTrace()
                    return@async
                }

            }
        }

        val currentServer = player.currentServer.orElse(null)?.serverInfo?.name

        var discordMessage = message

        if (generalConfig.enableJapanizer) {
            val japanized = VelocityMan10Manager.japanize(message)
            if (japanized != null) {
                message += " §6($japanized)"
                discordMessage += " ($japanized)"
            }
        }

        if (data.isMuted()) {
            e.result = PlayerChatEvent.ChatResult.denied()
            VelocityMan10Manager.warning("[Muted] <${player.username}> ($message)")
            player.sendMessage(
                VelocityMan10Manager.miniMessage(
                    messageConfig.muteMessage
                )
            )
            return null
        }

        if (data.isJailed()) {
            VelocityMan10Manager.warning("[Jailed] <${player.username}> ($message)")
            if (currentServer != serverConfig.jail) {
                val server = VelocityMan10Manager.proxy.getServer(serverConfig.jail)
                if (server.isPresent) {
                    player.createConnectionRequest(server.get()).fireAndForget()
                }
            }
            return null
        }

        if (e.message.toIntOrNull() != null) {
            return null
        }

        val serverName = mapOf(
            "server" to (currentServer ?: "N/A"),
            "name" to e.player.username
        )

        val proxyMessage = VelocityMan10Manager.miniMessage(
            chatConfig.proxyMessageFormat.applyPlaceholders(serverName)
        ).replaceText {
            it.match("%message%").replacement(message)
        }

        if (currentServer != null && !chatConfig.cancelSendingChatServer.contains(currentServer)) {
            for (player in VelocityMan10Manager.proxy.allPlayers) {
                val server = player.currentServer.orElse(null)?.serverInfo?.name ?: continue
                if (currentServer == server || chatConfig.cancelReceivingChatServer.contains(server)) continue
                player.sendMessage(proxyMessage)
            }

            DiscordBot.chat(
                chatConfig.minecraftToDiscordTextFormat.applyPlaceholders(
                    serverName + ("message" to discordMessage)
                )
            )
        }

        if (logConfig.chat) {
            DiscordBot.admin(
                logConfig.chatLogFormat.applyPlaceholders(
                    mapOf(
                        "server" to (currentServer ?: "N/A"),
                        "name" to e.player.username,
                        "message" to message
                    )
                )
            )
        }

        CompletableFuture.runAsync {
            Database.addMessageLog(player, e.message, currentServer ?: "N/A")
        }.exceptionally {
            VelocityMan10Manager.error("Error logging chat message: ${it.message}")
            it.printStackTrace()
            null
        }

        return null
    }

    @Subscribe
    fun onPlayerCommand(e: CommandExecuteEvent) {
        val messageConfig = Config.getOrThrow<MessageConfig>()
        val logConfig = Config.getOrThrow<LogConfig>()

        val player = e.commandSource
        if (player !is Player) return
        val data = Database.playerDataCache[player.uniqueId] ?: return

        val command = e.command
        val commandString = "/$command"

        if (data.isMuted()) {
            e.result = CommandExecuteEvent.CommandResult.denied()
            VelocityMan10Manager.warning("[Muted] <${player.username}> $commandString")
            player.sendRichMessage(messageConfig.muteMessage)
            return
        }

        if (data.isJailed()) {
            e.result = CommandExecuteEvent.CommandResult.denied()
            VelocityMan10Manager.warning("[Jailed] <${player.username}> $commandString")
            player.sendRichMessage(messageConfig.jailOnCommandMessage)
            return
        }

        if (logConfig.command) {
            val currentServer = player.currentServer.orElse(null)?.serverInfo?.name ?: "N/A"
            DiscordBot.admin(
                logConfig.commandLogFormat.applyPlaceholders(
                    mapOf(
                        "server" to currentServer,
                        "name" to player.username,
                        "command" to commandString
                    )
                )
            )
        }
        CompletableFuture.runAsync {
            Database.addCommandLog(player, commandString, player.getServerName())
        }.exceptionally {
            VelocityMan10Manager.error("Error logging command: ${it.message}")
            it.printStackTrace()
            null
        }
    }
}