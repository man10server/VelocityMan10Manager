package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.command.MessageManager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig

class TellCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("tell")
            .aliases("msg", "message", "m", "w", "t")
            .build()
    }

    fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.tellHelpMessage.replace("%command%", context.rootNode.name))
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("tell")
            .requires { source -> source.hasPermission("red.man10.velocity.command.tell") }
            .executes(this::help)
            .then(
                BrigadierCommand.requiredArgumentBuilder("宛先", StringArgumentType.word())
                    .suggests { _, builder ->
                        playerSuggestions(builder)
                    }
                    .executes(this::help)
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("内容", StringArgumentType.greedyString())
                            .executes { context ->
                                val config = Config.getOrThrow<CommandConfig>()
                                val sender = context.source

                                val targetName = StringArgumentType.getString(context, "宛先")
                                val message = StringArgumentType.getString(context, "内容")

                                val target = VelocityMan10Manager.proxy.getPlayer(targetName).orElse(null)
                                if (target == null) {
                                    sender.sendRichMessage(config.privateChatPlayerNotFound.replace("%name%", targetName))
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                if (sender is Player && sender.uniqueId == target.uniqueId) {
                                    sender.sendRichMessage(config.tellCannotSelf)
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                MessageManager.sendPrivateMessage(sender, target, message)

                                return@executes Command.SINGLE_SUCCESS
                            }
                    )
            )

        return BrigadierCommand(node)
    }
}