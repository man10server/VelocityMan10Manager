package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig

class Man10VelocityCommands: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("man10velocity")
            .aliases("mvelocity")
            .build()
    }

    private fun help(context: CommandContext<CommandSource>): Int {
        val config = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(
            config.helpMessage
                .replace("%command%", context.rootNode.name)
        )
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("man10velocity")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.man10velocity") }
            .executes(this::help)
            .then(
                BrigadierCommand.literalArgumentBuilder("help")
                    .executes(this::help)
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("reload")
                    .requires { sender -> sender.hasPermission("red.man10.velocity.command.reload") }
                    .executes {
                        val config = Config.getOrThrow<CommandConfig>()
                        VelocityMan10Manager.reloadAll()
                        it.source.sendRichMessage(config.reloaded)
                        Command.SINGLE_SUCCESS
                    }
            )
            .then(ReportCommand().createCommand().node)
            .then(BanCommand().createCommand().node)
            .then(MSBCommand().createCommand().node)
            .then(MuteCommand().createCommand().node)
            .then(JailCommand().createCommand().node)
            .then(WarnCommand().createCommand().node)
            .then(ChatSettingCommand().createCommand().node)
            .then(AltCommand().createCommand().node)
            .then(AlertCommand().createCommand().node)
            .then(ServerCommand().createCommand().node)

        return BrigadierCommand(node)
    }


}