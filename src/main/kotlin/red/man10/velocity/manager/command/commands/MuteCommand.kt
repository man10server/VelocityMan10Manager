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
import red.man10.velocity.manager.discord.DiscordBot
import java.time.format.DateTimeFormatter

class MuteCommand: PunishmentCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mmute").build()
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mmute")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.mute") }
            .then(
                createNode(
                    help = { context ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        context.source.sendRichMessage(commandConfig.muteHelpMessage)
                        Command.SINGLE_SUCCESS
                    },
                    execute = { context, target, duration, reason, isReset ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        val messageConfig = Config.getOrThrow<MessageConfig>()
                        val logConfig = Config.getOrThrow<LogConfig>()
                        val punishmentConfig = Config.getOrThrow<PunishmentConfig>()

                        if (isReset) {
                            if (target.muteUntil == null) {
                                context.source.sendRichMessage(commandConfig.muteAlreadyReleased.replace("%name%", target.player))
                                return@createNode
                            }

                            target.resetMute()

                            val placeholdersRelease = mapOf(
                                "name" to target.player,
                                "reason" to reason,
                                "punisher" to context.source.getName()
                            )

                            if (punishmentConfig.announceMute) {
                                VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                    messageConfig.muteReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                                DiscordBot.chat(
                                    messageConfig.muteReleaseDiscordMessage.applyPlaceholders(placeholdersRelease)
                                )
                            } else {
                                context.source.sendRichMessage(
                                    messageConfig.muteReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                            }

                            DiscordBot.jail(
                                logConfig.muteReleaseLogFormat.applyPlaceholders(placeholdersRelease)
                            )
                            return@createNode
                        }

                        target.addMuteTime(duration)

                        val date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(target.muteUntil)
                        val placeholders = mapOf(
                            "name" to target.player,
                            "reason" to reason,
                            "date" to date,
                            "punisher" to context.source.getName()
                        )

                        if (punishmentConfig.announceMute) {
                            VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                messageConfig.muteBroadcastMessage.applyPlaceholders(placeholders)
                            )
                            DiscordBot.chat(
                                messageConfig.muteBroadcastDiscordMessage.applyPlaceholders(placeholders)
                            )
                        } else {
                            context.source.sendRichMessage(
                                messageConfig.muteBroadcastMessage.applyPlaceholders(placeholders)
                            )
                        }

                        DiscordBot.jail(
                            logConfig.muteLogFormat.applyPlaceholders(placeholders)
                        )

                        val player = VelocityMan10Manager.proxy.getPlayer(target.uuid).orElse(null)
                        player?.sendRichMessage(
                            messageConfig.muteMessage.applyPlaceholders(placeholders)
                        )
                    }
                )
            )
        return BrigadierCommand(node)
    }
}

