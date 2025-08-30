package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.identity.Identity
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.discord.DiscordBot
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ReportCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("report")
            .aliases("報告")
            .build()
    }

    private fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.reportHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    private val lastSendReport = HashMap<UUID, String>()

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("report")
            .requires { source -> source.hasPermission("red.man10.velocity.command.report") }
            .executes(this::help)
            .then(
                BrigadierCommand.requiredArgumentBuilder("タイトル", StringArgumentType.word())
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("本文", StringArgumentType.greedyString())
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val player = context.source as? Player ?: run {
                                    context.source.sendRichMessage(commandConfig.cannotUseInConsole)
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                var subject = StringArgumentType.getString(context, "タイトル")
                                var content = StringArgumentType.getString(context, "本文")

                                if (lastSendReport[player.uniqueId] == content) {
                                    context.source.sendRichMessage(commandConfig.reportSameContent)
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                VelocityMan10Manager.japanize(subject)?.let {
                                    subject += " ($it)"
                                }
                                VelocityMan10Manager.japanize(content)?.let {
                                    content += " ($it)"
                                }
                                DiscordBot.report(
                                    """
                                        ```
                                        送信者: ${context.source.getOrDefault(Identity.NAME, "Unknown")}
                                        送信日: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}
                                        タイトル: $subject
                                        本文: $content
                                        ```
                                    """.trimIndent()
                                )

                                context.source.sendRichMessage(commandConfig.reportSend
                                    .replace("%title%", subject)
                                    .replace("%content%", content)
                                )

                                Command.SINGLE_SUCCESS
                            }
                    )
            )

        return BrigadierCommand(node)
    }
}