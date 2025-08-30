package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
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

class MSBCommand: PunishmentCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("msb")
            .build()
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("msb")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.msb") }
            .then(
                createNode(
                    help = { context ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        context.source.sendRichMessage(commandConfig.msbHelpMessage)
                        Command.SINGLE_SUCCESS
                    },
                    execute = { context, target, duration, reason, isReset ->
                        val messageConfig = Config.getOrThrow<MessageConfig>()
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        val logConfig = Config.getOrThrow<LogConfig>()

                        if (isReset) {
                            if (target.msbUntil == null) {
                                context.source.sendRichMessage(commandConfig.msbAlreadyReleased.replace("%name%", target.player))
                                return@createNode
                            }

                            target.resetBan()

                            val placeholdersRelease = mapOf(
                                "name" to target.player,
                                "reason" to reason,
                                "punisher" to context.source.getName()
                            )

                            context.source.sendRichMessage(
                                commandConfig.msbRelease.applyPlaceholders(placeholdersRelease)
                            )
                            DiscordBot.jail(
                                logConfig.msbReleaseLogFormat.applyPlaceholders(placeholdersRelease)
                            )
                            return@createNode
                        }

                        target.addMSBTime(duration)

                        val date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(target.banUntil)
                        val placeholders = mapOf(
                            "name" to target.player,
                            "reason" to reason,
                            "date" to date,
                            "punisher" to context.source.getName()
                        )

                        context.source.sendRichMessage(
                            commandConfig.msbBanned.applyPlaceholders(placeholders)
                        )

                        DiscordBot.jail(
                            logConfig.msbLogFormat.applyPlaceholders(placeholders)
                        )

                        val player = VelocityMan10Manager.proxy.getPlayer(target.uuid).orElse(null)
                        player?.disconnect(
                            VelocityMan10Manager.miniMessage(
                                messageConfig.msbMessage.applyPlaceholders(placeholders)
                            )
                        )
                    }
                )
            )
        return BrigadierCommand(node)
    }
}