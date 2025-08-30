package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig

class ChatSettingCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mchat")
            .build()
    }

    fun help(context: CommandContext<CommandSource>): Int {
        val config = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(config.chatHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mchat")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.mchat") }
            .then(
                BrigadierCommand.requiredArgumentBuilder("サーバー", StringArgumentType.word())
                    .suggests { _, builder ->
                        val servers = VelocityMan10Manager.proxy.allServers.map { it.serverInfo.name }
                        servers.forEach { builder.suggest(it) }
                        builder.buildFuture()
                    }
                    .then(
                        BrigadierCommand.literalArgumentBuilder("cancelSend")
                            .then(
                                BrigadierCommand.requiredArgumentBuilder("切替", StringArgumentType.word())
                                    .suggests { _, builder ->
                                        builder.suggest("true")
                                        builder.suggest("false")
                                        builder.buildFuture()
                                    }
                                    .executes { context ->
                                        val commandConfig = Config.getOrThrow<CommandConfig>()

                                        val sender = context.source
                                        val serverName = StringArgumentType.getString(context, "サーバー")
                                        val toggle = StringArgumentType.getString(context, "切替")
                                        if (toggle != "true" && toggle != "false") {
                                            sender.sendRichMessage(commandConfig.chatToggleNotBoolean.replace("%toggle%", toggle))
                                            return@executes Command.SINGLE_SUCCESS
                                        }
                                        val toggleBool = toggle.toBoolean()

                                        val server = VelocityMan10Manager.proxy.getServer(serverName).orElse(null)

                                        if (server == null) {
                                            sender.sendRichMessage(commandConfig.chatServerNotFound.replace("%server%", serverName))
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        val loader = YamlConfigurationLoader.builder()
                                            .indent(2)
                                            .nodeStyle(NodeStyle.BLOCK)
                                            .path(VelocityMan10Manager.dataDirectory.resolve("config/chat.yml"))
                                            .build()

                                        val config = loader.load()

                                        val cancelSendingList = config.node("cancelSendingChatServer").getList(String::class.java) ?: listOf()

                                        if (toggleBool) {
                                            if (cancelSendingList.contains(serverName)) {
                                                sender.sendRichMessage(commandConfig.chatCancelSendAlreadyTrue.replace("%server%", serverName))
                                                return@executes Command.SINGLE_SUCCESS
                                            }

                                            val newList = cancelSendingList.toMutableList()
                                            newList.add(serverName)
                                            config.node("cancelSendingChatServer").set(newList)
                                            loader.save(config)

                                            Config.load()

                                            sender.sendRichMessage(commandConfig.chatCancelSendToggleTrue.replace("%server%", serverName))
                                        } else {
                                            if (!cancelSendingList.contains(serverName)) {
                                                sender.sendRichMessage(commandConfig.chatCancelSendAlreadyFalse.replace("%server%", serverName))
                                                return@executes Command.SINGLE_SUCCESS
                                            }

                                            val newList = cancelSendingList.toMutableList()
                                            newList.remove(serverName)
                                            config.node("cancelSendingChatServer").set(newList)
                                            loader.save(config)

                                            Config.load()

                                            sender.sendRichMessage(commandConfig.chatCancelSendToggleFalse.replace("%server%", serverName))
                                        }
                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
                    .then(
                        BrigadierCommand.literalArgumentBuilder("cancelReceive")
                            .then(
                                BrigadierCommand.requiredArgumentBuilder("切替", StringArgumentType.word())
                                    .suggests { _, builder ->
                                        builder.suggest("true")
                                        builder.suggest("false")
                                        builder.buildFuture()
                                    }
                                    .executes { context ->
                                        val commandConfig = Config.getOrThrow<CommandConfig>()

                                        val sender = context.source
                                        val serverName = StringArgumentType.getString(context, "サーバー")
                                        val toggle = StringArgumentType.getString(context, "切替")
                                        if (toggle != "true" && toggle != "false") {
                                            sender.sendRichMessage(commandConfig.chatToggleNotBoolean.replace("%toggle%", toggle))
                                            return@executes Command.SINGLE_SUCCESS
                                        }
                                        val toggleBool = toggle.toBoolean()

                                        val server = VelocityMan10Manager.proxy.getServer(serverName).orElse(null)

                                        if (server == null) {
                                            sender.sendRichMessage(commandConfig.chatServerNotFound.replace("%server%", serverName))
                                            return@executes Command.SINGLE_SUCCESS
                                        }

                                        val loader = YamlConfigurationLoader.builder()
                                            .indent(2)
                                            .nodeStyle(NodeStyle.BLOCK)
                                            .path(VelocityMan10Manager.dataDirectory.resolve("config/chat.yml"))
                                            .build()

                                        val config = loader.load()

                                        val cancelReceivingList = config.node("cancelReceivingChatServer").getList(String::class.java) ?: listOf()

                                        if (toggleBool) {
                                            if (cancelReceivingList.contains(serverName)) {
                                                sender.sendRichMessage(commandConfig.chatCancelReceiveAlreadyTrue.replace("%server%", serverName))
                                                return@executes Command.SINGLE_SUCCESS
                                            }

                                            val newList = cancelReceivingList.toMutableList()
                                            newList.add(serverName)
                                            config.node("cancelReceivingChatServer").set(newList)
                                            loader.save(config)

                                            Config.load()

                                            sender.sendRichMessage(commandConfig.chatCancelReceiveToggleTrue.replace("%server%", serverName))
                                        } else {
                                            if (!cancelReceivingList.contains(serverName)) {
                                                sender.sendRichMessage(commandConfig.chatCancelReceiveAlreadyFalse.replace("%server%", serverName))
                                                return@executes Command.SINGLE_SUCCESS
                                            }

                                            val newList = cancelReceivingList.toMutableList()
                                            newList.remove(serverName)
                                            config.node("cancelReceivingChatServer").set(newList)
                                            loader.save(config)

                                            Config.load()

                                            sender.sendRichMessage(commandConfig.chatCancelReceiveToggleFalse.replace("%server%", serverName))
                                        }
                                        Command.SINGLE_SUCCESS
                                    }
                            )
                    )
            )
        return BrigadierCommand(node)
    }
}