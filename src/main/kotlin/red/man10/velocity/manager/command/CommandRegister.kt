package red.man10.velocity.manager.command

import com.velocitypowered.api.proxy.ProxyServer
import red.man10.velocity.manager.Utils
import red.man10.velocity.manager.command.commands.message.ReplyCommand
import red.man10.velocity.manager.command.commands.message.TellCommand

object CommandRegister {

    const val packageName = "red.man10.velocity.manager.command.commands"

    fun register(proxy: ProxyServer) {
        val commandManager = proxy.commandManager
        val commands = Utils.getClasses(this::class.java.protectionDomain.codeSource.location, packageName)
        commands
            .filter { it.packageName == packageName }
            .forEach { command ->
                if (AbstractCommand::class.java.isAssignableFrom(command)) {
                    val constructor = command.getConstructor()
                    val commandInstance = constructor.newInstance() as AbstractCommand
                    commandManager.register(commandInstance.getMeta(commandManager), commandInstance.createCommand())
                }
            }

        for (command in listOf("tell", "msg", "message", "m", "w", "t")) {
            val tellCommand = TellCommand(command)
            commandManager.register(tellCommand.getMeta(commandManager), tellCommand.createCommand())
        }

        for (command in listOf("reply", "r")) {
            val replyCommand = ReplyCommand(command)
            commandManager.register(replyCommand.getMeta(commandManager), replyCommand.createCommand())
        }
    }
}