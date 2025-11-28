package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.Utils.getName
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.PunishmentCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.config.sub.LogConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.PunishmentConfig
import red.man10.velocity.manager.discord.DiscordBot
import java.time.format.DateTimeFormatter

class BanCommand: PunishmentCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mban")
            .build()
    }

    private fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.banHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mban")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.ban") }
            .executes(this::help)
            .then(
                createNode(
                    help = ::help,
                    execute = { context, target, duration, reason, isReset ->
                        val messageConfig = Config.getOrThrow<MessageConfig>()
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        val logConfig = Config.getOrThrow<LogConfig>()
                        val punishmentConfig = Config.getOrThrow<PunishmentConfig>()

                        if (isReset) {
                            if (target.banUntil == null) {
                                context.source.sendRichMessage(commandConfig.banAlreadyReleased.replace("%name%", target.player))
                                return@createNode
                            }

                            target.resetBan()

                            val placeholdersRelease = mapOf(
                                "name" to target.player,
                                "reason" to reason,
                                "punisher" to context.source.getName()
                            )

                            if (punishmentConfig.announceBan) {
                                VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                    messageConfig.banReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                                DiscordBot.chat(
                                    messageConfig.banReleaseDiscordMessage.applyPlaceholders(placeholdersRelease)
                                )
                            } else {
                                context.source.sendRichMessage(
                                    messageConfig.banReleaseMessage.applyPlaceholders(placeholdersRelease)
                                )
                            }
                            DiscordBot.jail(
                                logConfig.banReleaseLogFormat.applyPlaceholders(placeholdersRelease)
                            )
                            return@createNode
                        }

                        target.addBanTime(duration)

                        val date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(target.banUntil)
                        val placeholders = mapOf(
                            "name" to target.player,
                            "reason" to reason,
                            "date" to date,
                            "punisher" to context.source.getName()
                        )

                        if (punishmentConfig.announceBan) {
                            VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                messageConfig.banBroadcastMessage.applyPlaceholders(placeholders)
                            )
                            DiscordBot.chat(
                                messageConfig.banBroadcastDiscordMessage.applyPlaceholders(placeholders)
                            )
                        } else {
                            context.source.sendRichMessage(
                                messageConfig.banBroadcastMessage.applyPlaceholders(placeholders)
                            )
                        }

                        DiscordBot.jail(
                            logConfig.banLogFormat.applyPlaceholders(placeholders)
                        )

                        val player = VelocityMan10Manager.proxy.getPlayer(target.uuid).orElse(null)
                        player?.disconnect(
                            VelocityMan10Manager.miniMessage(
                                messageConfig.banMessage.applyPlaceholders(placeholders)
                            )
                        )
                    }
                )
            )
        return BrigadierCommand(node)
    }
}