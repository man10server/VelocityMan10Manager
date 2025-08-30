package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.command.PunishmentCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.config.sub.LogConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.PunishmentConfig
import red.man10.velocity.manager.database.Database
import red.man10.velocity.manager.discord.DiscordBot
import java.util.concurrent.CompletableFuture

class WarnCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mwarn")
            .build()
    }

    fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.warnHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mwarn")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.mwarn") }
            .then(
                BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
                    .executes(this::help)
                    .suggests { _, builder ->
                        playerSuggestions(builder)
                    }
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("スコア", IntegerArgumentType.integer(0))
                            .executes(this::help)
                            .then(
                                BrigadierCommand.requiredArgumentBuilder("理由", StringArgumentType.greedyString())
                                    .executes { context ->
                                        val messageConfig = Config.getOrThrow<MessageConfig>()
                                        val commandConfig = Config.getOrThrow<CommandConfig>()
                                        val logConfig = Config.getOrThrow<LogConfig>()
                                        val punishmentConfig = Config.getOrThrow<PunishmentConfig>()
                                        val sender = context.source
                                        val target = StringArgumentType.getString(context, "対象")
                                        val score = IntegerArgumentType.getInteger(context, "スコア")
                                        val reason = StringArgumentType.getString(context, "理由")

                                        val playerData = PunishmentCommand.getPlayerData(target)

                                        if (playerData == null) {
                                            sender.sendRichMessage(commandConfig.playerNotFound.replace("%name%", target))
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        CompletableFuture.runAsync {
                                            Database.giveScore(playerData.uuid, score, reason)

                                            val placeholders = mapOf(
                                                "name" to target,
                                                "score" to score.toString(),
                                                "reason" to reason
                                            )

                                            if (punishmentConfig.announceWarn) {
                                                VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                                    messageConfig.warnBroadcastMessage.applyPlaceholders(placeholders)
                                                )
                                                DiscordBot.chat(
                                                    messageConfig.warnBroadcastDiscordMessage.applyPlaceholders(placeholders)
                                                )
                                            } else {
                                                context.source.sendRichMessage(
                                                    messageConfig.warnBroadcastMessage.applyPlaceholders(placeholders)
                                                )
                                            }

                                            DiscordBot.jail(
                                                logConfig.warnLogFormat.applyPlaceholders(placeholders)
                                            )

                                            val player = VelocityMan10Manager.proxy.getPlayer(playerData.uuid).orElse(null)
                                            player?.sendRichMessage(
                                                messageConfig.warnMessage.applyPlaceholders(placeholders)
                                            )

                                        }.exceptionally { e ->
                                            e.printStackTrace()
                                            sender.sendRichMessage(commandConfig.errorOccurred)
                                            null
                                        }

                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
            )
        return BrigadierCommand(node)
    }
}