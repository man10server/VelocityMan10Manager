package red.man10.velocity.manager.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.config.sub.MessageConfig
import red.man10.velocity.manager.config.sub.PunishmentConfig
import red.man10.velocity.manager.database.Database
import red.man10.velocity.manager.database.models.PlayerData
import java.util.concurrent.CompletableFuture

abstract class PunishmentCommand: AbstractCommand() {


    companion object {

        const val RESET = -1L
        const val INVALID = -2L

        //1m(分) 1h(時間) 1d(日) 0k(1050 years) reset, 秒で返す
        fun parseDuration(input: String): Long {
            try {
                if (input == "reset") {
                    return RESET
                }
                if (input == "0k") {
                    return 60L * 60L * 24L * 365L * 1050L
                }

                val unit = input.last()
                val number = input.dropLast(1).toLong()
                return when (unit) {
                    'm' -> number * 60L
                    'h' -> number * 60L * 60L
                    'd' -> number * 60L * 60L * 24L
                    else -> return INVALID
                }
            } catch (_: Exception) {
                return INVALID
            }
        }

        fun getPlayerData(name: String): PlayerData? {
            val player = VelocityMan10Manager.proxy.getPlayer(name).orElse(null)
            if (player != null) {
                val playerData = Database.playerDataCache[player.uniqueId]
                if (playerData != null) {
                    return playerData
                }
            }

            return Database.getPlayerDataByName(name)
        }
    }

    protected fun presetSuggests(builder: SuggestionsBuilder) {
        val config = Config.getOrThrow<PunishmentConfig>()
        config.presetPunishments.keys.forEach {
            builder.suggest(it)
        }
    }

    protected fun createNode(help: (CommandContext<CommandSource>) -> Int, execute: (context: CommandContext<CommandSource>, target: PlayerData, duration: Long, reason: String, isReset: Boolean) -> Unit): RequiredArgumentBuilder<CommandSource, String> {
        return BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
            .suggests { _, builder ->
                playerSuggestions(builder)
            }
            .executes(help)
            .then(
                BrigadierCommand.requiredArgumentBuilder("期間", StringArgumentType.word())
                    .suggests { _, builder ->
                        builder.suggest("1m")
                        builder.suggest("1h")
                        builder.suggest("1d")
                        builder.suggest("0k")
                        builder.suggest("reset")
                        builder.buildFuture()
                    }
                    .executes(help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("理由", StringArgumentType.greedyString())
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()

                                val sender = context.source
                                val target = StringArgumentType.getString(context, "対象")
                                val durationStr = StringArgumentType.getString(context, "期間")
                                val reason = StringArgumentType.getString(context, "理由")

                                CompletableFuture.runAsync {
                                    val playerData = getPlayerData(target)
                                    if (playerData == null) {
                                        sender.sendRichMessage(commandConfig.playerNotFound)
                                        return@runAsync
                                    }

                                    val duration = parseDuration(durationStr)
                                    if (duration == INVALID) {
                                        sender.sendRichMessage(commandConfig.punishmentInvalidDuration)
                                        return@runAsync
                                    }

                                    if (reason.isEmpty()) {
                                        sender.sendRichMessage(commandConfig.punishmentInvalidReason)
                                        return@runAsync
                                    }

                                    val isReset = duration == RESET

                                    execute(context, playerData, duration, reason, isReset)
                                }.exceptionally { ex ->
                                    ex.printStackTrace()
                                    null
                                }
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("preset")
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("プリセット", StringArgumentType.word())
                            .suggests { _, builder ->
                                presetSuggests(builder)
                                builder.buildFuture()
                            }
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val punishmentConfig = Config.getOrThrow<PunishmentConfig>()

                                val sender = context.source
                                val target = StringArgumentType.getString(context, "対象")
                                val preset = StringArgumentType.getString(context, "プリセット")

                                CompletableFuture.runAsync {
                                    val playerData = getPlayerData(target)
                                    if (playerData == null) {
                                        sender.sendRichMessage(commandConfig.playerNotFound)
                                        return@runAsync
                                    }

                                    val presetData = punishmentConfig.presetPunishments[preset]
                                    if (presetData == null) {
                                        sender.sendRichMessage(commandConfig.punishmentPresetNotFound)
                                        return@runAsync
                                    }

                                    val duration = presetData.first
                                    val reason = presetData.second

                                    if (reason.isEmpty()) {
                                        sender.sendRichMessage(commandConfig.punishmentInvalidReason)
                                        return@runAsync
                                    }

                                    val isReset = duration == RESET

                                    execute(context, playerData, duration, reason, isReset)
                                }
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
    }
}