package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import net.kyori.adventure.text.Component
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.MessageConfig

class AlertCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("alert")
            .build()
    }



    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("alert")
            .requires { source -> source.hasPermission("red.man10.velocity.command.alert") }
            .then(
                BrigadierCommand.requiredArgumentBuilder("内容", StringArgumentType.greedyString())
                    .executes { context ->
                        val messageConfig = Config.getOrThrow<MessageConfig>()

                        val message = VelocityMan10Manager.miniMessage(
                            StringArgumentType.getString(context, "内容")
                        )

                        val formattedMessage = Component.text(messageConfig.alertMessageFormat)
                            .replaceText { replace ->
                                replace.matchLiteral("%message%")
                                    .replacement(message)
                            }

                        VelocityMan10Manager.proxy.allPlayers.forEach {
                            it.sendMessage(formattedMessage)
                        }
                        Command.SINGLE_SUCCESS
                    }
            )


        return BrigadierCommand(node)
    }
}