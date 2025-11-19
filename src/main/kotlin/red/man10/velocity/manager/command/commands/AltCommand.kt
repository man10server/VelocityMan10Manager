package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.Utils.getName
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
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

class AltCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("malt")
            .build()
    }

    fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.altHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("malt")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.alt") }
            .executes(this::help)
            .then(
                BrigadierCommand.literalArgumentBuilder("sub")
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
                            .suggests { _, builder ->
                                playerSuggestions(builder)
                            }
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val sender = context.source
                                val target = StringArgumentType.getString(context, "対象")
                                CompletableFuture.runAsync {
                                    val subAccounts = Database.getSubAccounts(target)
                                        .filter { it.playerData.player != target }
                                        .groupBy { it.playerData.uuid }
                                        .map { it.value[0] }
                                    if (subAccounts.isEmpty()) {
                                        sender.sendRichMessage(commandConfig.altSubNotFound)
                                    } else {
                                        sender.sendRichMessage(commandConfig.altSubAccount.replace("%name%", target))
                                        val builder = StringBuilder()
                                        subAccounts.forEach {
                                            builder.append(commandConfig.altSubAccountFormat
                                                .replace("%uuid%", it.playerData.uuid.toString())
                                                .replace("%name%", it.playerData.player)
                                            ).append("\n")
                                        }
                                        sender.sendRichMessage(builder.toString())
                                    }
                                }.exceptionally { ex ->
                                    ex.printStackTrace()
                                    sender.sendRichMessage(commandConfig.errorOccurred)
                                    null
                                }
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("user")
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
                            .suggests { _, builder ->
                                playerSuggestions(builder)
                            }
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val sender = context.source
                                val target = StringArgumentType.getString(context, "対象")
                                CompletableFuture.runAsync {
                                    val accounts = Database.getSubAccounts(target)
                                    if (accounts.isEmpty()) {
                                        sender.sendRichMessage(commandConfig.altUserNotFound)
                                    } else {
                                        sender.sendRichMessage(commandConfig.altUserSearch.replace("%name%", target))
                                        val builder = StringBuilder()
                                        accounts.forEach {
                                            builder.append(commandConfig.altUserSearchFormat
                                                .replace("%uuid%", it.playerData.uuid.toString())
                                                .replace("%name%", it.playerData.player)
                                                .replace("%ip%", it.ip)
                                                .replace("%count%", it.connectionCount.toString())
                                            ).append("\n")
                                        }
                                        sender.sendRichMessage(builder.toString())
                                    }
                                }.exceptionally { ex ->
                                    ex.printStackTrace()
                                    sender.sendRichMessage(commandConfig.errorOccurred)
                                    null
                                }
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("ban")
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
                            .suggests { _, builder ->
                                playerSuggestions(builder)
                            }
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
                                        val reason = StringArgumentType.getString(context, "理由")
                                        CompletableFuture.runAsync {
                                            val accounts = Database.getSubAccounts(target)
                                            if (accounts.isEmpty()) {
                                                sender.sendRichMessage(commandConfig.altUserNotFound)
                                            } else {
                                                val toBan = accounts.map { it.playerData }.distinctBy { it.uuid }
                                                toBan.forEach {
                                                    it.addBanTime(PunishmentCommand.parseDuration("0k"))
                                                    val date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(it.banUntil)
                                                    val placeholders = mapOf(
                                                        "name" to it.player,
                                                        "reason" to reason,
                                                        "date" to date,
                                                        "punisher" to sender.getName()
                                                    )
                                                    if (punishmentConfig.announceAltBan) {
                                                        VelocityMan10Manager.sendMessageToMinecraftPlayers(
                                                            messageConfig.banBroadcastMessage.applyPlaceholders(placeholders)
                                                        )
                                                        DiscordBot.chat(
                                                            messageConfig.banBroadcastDiscordMessage.applyPlaceholders(placeholders)
                                                        )
                                                    } else {
                                                        sender.sendRichMessage(
                                                            messageConfig.banBroadcastMessage.applyPlaceholders(placeholders)
                                                        )
                                                    }
                                                    DiscordBot.jail(
                                                        logConfig.banLogFormat.applyPlaceholders(placeholders)
                                                    )

                                                    val player = VelocityMan10Manager.proxy.getPlayer(it.uuid).orElse(null)
                                                    player?.disconnect(
                                                        VelocityMan10Manager.miniMessage(
                                                            messageConfig.banMessage.applyPlaceholders(placeholders)
                                                        )
                                                    )
                                                }


                                            }
                                        }.exceptionally { ex ->
                                            ex.printStackTrace()
                                            sender.sendRichMessage(commandConfig.errorOccurred)
                                            null
                                        }
                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("ipban")
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("対象/IP", StringArgumentType.word())
                            .suggests { _, builder ->
                                playerSuggestions(builder)
                            }
                            .executes(this::help)
                            .then(
                                BrigadierCommand.requiredArgumentBuilder("理由", StringArgumentType.greedyString())
                                    .executes { context ->
                                        val messageConfig = Config.getOrThrow<MessageConfig>()
                                        val commandConfig = Config.getOrThrow<CommandConfig>()
                                        val logConfig = Config.getOrThrow<LogConfig>()
                                        val sender = context.source
                                        val target = StringArgumentType.getString(context, "対象/IP")
                                        val reason = StringArgumentType.getString(context, "理由")
                                        CompletableFuture.runAsync {
                                            val player = VelocityMan10Manager.proxy.getPlayer(target).orElse(null)
                                            val ip = if (player != null) {
                                                player.remoteAddress.address.hostAddress
                                            } else {
                                                // Check if the target is a valid IP address
                                                val regex = Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b""")
                                                if (regex.matches(target)) {
                                                    target
                                                } else {
                                                    sender.sendRichMessage(commandConfig.altIpBanPlayerOrIpNotFound)
                                                    return@runAsync
                                                }
                                            }

                                            Database.addBanIP(ip, reason)

                                            for (serverPlayer in VelocityMan10Manager.proxy.allPlayers) {
                                                if (serverPlayer.remoteAddress.address.hostAddress == ip) {
                                                    serverPlayer.disconnect(
                                                        VelocityMan10Manager.miniMessage(
                                                            messageConfig.banMessage
                                                        )
                                                    )
                                                }
                                            }

                                            val placeholders = mapOf(
                                                "ip" to ip,
                                                "reason" to reason,
                                                "punisher" to sender.getName()
                                            )

                                            sender.sendRichMessage(
                                                commandConfig.altIpBanned.applyPlaceholders(placeholders)
                                            )

                                            DiscordBot.jail(
                                                logConfig.ipBanLogFormat.applyPlaceholders(placeholders)
                                            )
                                        }

                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("releaseIpBan")
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("IP", StringArgumentType.word())
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val logConfig = Config.getOrThrow<LogConfig>()
                                val sender = context.source
                                val ip = StringArgumentType.getString(context, "IP")
                                CompletableFuture.runAsync {
                                    val success = Database.removeBanIP(ip)
                                    if (success) {
                                        sender.sendRichMessage(commandConfig.altIpBanRelease.replace("%ip%", ip))
                                        DiscordBot.jail(
                                            logConfig.ipBanReleaseLogFormat
                                                .replace("%ip%", ip)
                                                .replace("%punisher%", sender.getName())
                                        )
                                    } else {
                                        sender.sendRichMessage(commandConfig.altIpBanReleaseNotFound.replace("%ip%", ip))
                                    }
                                }.exceptionally { ex ->
                                    ex.printStackTrace()
                                    sender.sendRichMessage(commandConfig.errorOccurred)
                                    null
                                }
                                Command.SINGLE_SUCCESS
                            }
                    )
            )
        return BrigadierCommand(node)
    }
}