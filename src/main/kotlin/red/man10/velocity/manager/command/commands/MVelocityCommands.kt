package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import net.kyori.adventure.text.Component
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand

class MVelocityCommands: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mvelocity")
            .build()
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mvelocity")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.mvelocity") }
            .then(
                BrigadierCommand.literalArgumentBuilder("reload")
                    .requires { sender -> sender.hasPermission("red.man10.velocity.command.reload") }
                    .executes {
                        VelocityMan10Manager.reloadAll()
                        it.source.sendMessage(Component.text("Config reloaded"))
                        Command.SINGLE_SUCCESS
                    }
            )
            .then(ReportCommand().createCommand().node)

        return BrigadierCommand(node)
    }


}