package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.Utils.getName
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.PunishmentCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.config.sub.LogConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.PunishmentConfig
import red.man10.velocity.manager.config.sub.ServerConfig
import red.man10.velocity.manager.discord.DiscordBot
import java.time.format.DateTimeFormatter

class JailCommand: PunishmentCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mjail")
            .build()
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mjail")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.jail") }
            .then(
                createNode(
                    help = { context ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        context.source.sendRichMessage(commandConfig.jailHelpMessage)
                        return@createNode Command.SINGLE_SUCCESS
                    },
                    execute = { context, target, duration, reason, isReset ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        val messageConfig = Config.getOrThrow<MessageConfig>()
                        val logConfig = Config.getOrThrow<LogConfig>()
                        val punishmentConfig = Config.getOrThrow<PunishmentConfig>()

                        if (isReset) {
                            if (target.jailUntil == null) {
                                context.source.sendRichMessage(commandConfig.jailAlreadyReleased.replace("%name%", target.player))
                                return@createNode
                            }

                            target.resetJail()

                            val placeholdersRelease = mapOf(
                                "name" to target.player,
                                "reason" to reason,
                                "punisher" to context.source.getName()
                            )

                            if (punishmentConfig.announceJail) {
                                VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                    messageConfig.jailReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                                DiscordBot.chat(
                                    messageConfig.jailReleaseDiscordMessage.applyPlaceholders(placeholdersRelease)
                                )
                            } else {
                                context.source.sendRichMessage(
                                    messageConfig.jailReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                            }

                            DiscordBot.jail(
                                logConfig.jailReleaseLogFormat.applyPlaceholders(placeholdersRelease)
                            )
                            return@createNode
                        }

                        target.addJailTime(duration)

                        val date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(target.banUntil)
                        val placeholders = mapOf(
                            "name" to target.player,
                            "reason" to reason,
                            "date" to date,
                            "punisher" to context.source.getName()
                        )

                        if (punishmentConfig.announceJail) {
                            VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                messageConfig.jailBroadcastMessage.applyPlaceholders(placeholders)
                            )
                            DiscordBot.chat(
                                messageConfig.jailBroadcastDiscordMessage.applyPlaceholders(placeholders)
                            )
                        } else {
                            context.source.sendRichMessage(
                                messageConfig.jailBroadcastMessage.applyPlaceholders(placeholders)
                            )
                        }

                        DiscordBot.jail(
                            logConfig.jailLogFormat.applyPlaceholders(placeholders)
                        )

                        val player = VelocityMan10Manager.proxy.getPlayer(target.uuid).orElse(null)
                        if (player != null) {
                            val serverConfig = Config.getOrThrow<ServerConfig>()
                            val jailServer = VelocityMan10Manager.proxy.getServer(serverConfig.jail).orElse(null)
                            if (jailServer == null) {
                                player.disconnect(
                                    VelocityMan10Manager.miniMessage(
                                        messageConfig.jailedMessage.applyPlaceholders(placeholders)
                                    )
                                )
                            } else {
                                player.createConnectionRequest(jailServer).fireAndForget()
                                player.sendRichMessage(
                                    messageConfig.jailedMessage.applyPlaceholders(placeholders)
                                )
                            }
                        }
                    }
                )
            )
        return BrigadierCommand(node)
    }
}