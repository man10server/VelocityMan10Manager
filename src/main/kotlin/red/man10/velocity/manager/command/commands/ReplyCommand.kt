package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.proxy.Player
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.command.MessageManager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig

class ReplyCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("reply")
            .aliases("r")
            .build()
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("reply")
            .requires { source -> source.hasPermission("red.man10.velocity.command.reply") }
            .executes {
                val config = Config.getOrThrow<CommandConfig>()
                val player = it.source as? Player ?: run {
                    it.source.sendRichMessage(config.cannotUseInConsole)
                    return@executes Command.SINGLE_SUCCESS
                }
                val history = MessageManager.getLastSender(player.uniqueId)?.second
                if (history == null) {
                    player.sendRichMessage(config.replyCurrentlyNoOne)
                } else {
                    player.sendRichMessage(config.replyCurrentlyPlayer.replace("%name%", history))
                }

                Command.SINGLE_SUCCESS
            }
            .then(
                BrigadierCommand.requiredArgumentBuilder("内容", StringArgumentType.greedyString())
                    .executes { context ->
                        val config = Config.getOrThrow<CommandConfig>()
                        val sender = context.source
                        val message = StringArgumentType.getString(context, "内容")

                        val player = sender as? Player ?: run {
                            sender.sendRichMessage(config.cannotUseInConsole)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val history = MessageManager.getLastSender(player.uniqueId)
                        if (history == null) {
                            player.sendRichMessage(config.replyCurrentlyNoOne)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val target = VelocityMan10Manager.Companion.proxy.getPlayer(history.first).orElse(null)
                        if (target == null) {
                            sender.sendRichMessage(config.privateChatPlayerNotFound.replace("%name%", history.second))
                            return@executes Command.SINGLE_SUCCESS
                        }

                        MessageManager.sendPrivateMessage(sender, target, message)
                        return@executes Command.SINGLE_SUCCESS
                    }
            )
        return BrigadierCommand(node)
    }
}