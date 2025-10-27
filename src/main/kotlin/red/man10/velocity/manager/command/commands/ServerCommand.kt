package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.server.ServerInfo
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import red.man10.velocity.manager.config.sub.ServerConfig
import java.net.InetSocketAddress

class ServerCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("mserver")
            .build()
    }

    private fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.serverHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("mserver")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.server") }
            .executes(this::help)
            .then(
                BrigadierCommand.literalArgumentBuilder("list")
                    .executes { context ->
                        val commandConfig = Config.getOrThrow<CommandConfig>()
                        val sender = context.source

                        val serverConfig = Config.getOrThrow<ServerConfig>()
                        val servers = serverConfig.servers

                        if (servers.isEmpty()) {
                            sender.sendRichMessage(commandConfig.serverIsEmpty)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        sender.sendRichMessage(commandConfig.serverList)

                        val builder = StringBuilder()
                        servers.forEach { server ->
                            builder.append(commandConfig.serverListFormat
                                .replace("%name%", server.name)
                                .replace("%address%", server.address.hostName)
                                .replace("%port%", server.address.port.toString())
                            ).append("\n")
                        }
                        sender.sendRichMessage(builder.toString())

                        return@executes Command.SINGLE_SUCCESS
                    }
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("add")
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("サーバー名", StringArgumentType.word())
                            .then(
                                BrigadierCommand.requiredArgumentBuilder("ホスト名", StringArgumentType.word())
                                    .then(
                                        BrigadierCommand.requiredArgumentBuilder("ポート", IntegerArgumentType.integer(1, 65535))
                                            .executes { context ->
                                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                                val serverConfig = Config.getOrThrow<ServerConfig>()
                                                val sender = context.source

                                                val name = StringArgumentType.getString(context, "サーバー名")
                                                val host = StringArgumentType.getString(context, "ホスト名")
                                                val port = IntegerArgumentType.getInteger(context, "ポート")

                                                if (serverConfig.servers.any { it.name.equals(name, true) }) {
                                                    sender.sendRichMessage(commandConfig.serverAlreadyExists.replace("%name%", name))
                                                    return@executes Command.SINGLE_SUCCESS
                                                }

                                                val loader = YamlConfigurationLoader.builder()
                                                    .indent(2)
                                                    .nodeStyle(NodeStyle.BLOCK)
                                                    .path(VelocityMan10Manager.dataDirectory.resolve("config/server.yml"))
                                                    .build()

                                                val config = loader.load()

                                                val servers = config.node("servers").childrenMap().map { it.key.toString() to it.value }.toMap().toMutableMap()
                                                val newServer = mapOf(
                                                    "address" to host,
                                                    "port" to port
                                                )
                                                servers[name] = loader.createNode().set(newServer)
                                                config.node("servers").set(servers)
                                                loader.save(config)

                                                serverConfig.servers.add(ServerInfo(name, InetSocketAddress(host, port)))
                                                VelocityMan10Manager.reloadServers()

                                                sender.sendRichMessage(commandConfig.serverAdded
                                                    .replace("%name%", name)
                                                    .replace("%address%", host)
                                                    .replace("%port%", port.toString())
                                                )
                                                return@executes Command.SINGLE_SUCCESS
                                            }
                                    )
                            )
                    )
            )
            .then(
                BrigadierCommand.literalArgumentBuilder("remove")
                    .then(
                        BrigadierCommand.requiredArgumentBuilder("サーバー名", StringArgumentType.word())
                            .executes { context ->
                                val commandConfig = Config.getOrThrow<CommandConfig>()
                                val serverConfig = Config.getOrThrow<ServerConfig>()
                                val sender = context.source

                                val name = StringArgumentType.getString(context, "サーバー名")

                                val target = serverConfig.servers.firstOrNull { it.name.equals(name, true) }
                                if (target == null) {
                                    sender.sendRichMessage(commandConfig.serverNotFound.replace("%name%", name))
                                    return@executes Command.SINGLE_SUCCESS
                                }

                                val loader = YamlConfigurationLoader.builder()
                                    .indent(2)
                                    .nodeStyle(NodeStyle.BLOCK)
                                    .path(VelocityMan10Manager.dataDirectory.resolve("config/server.yml"))
                                    .build()

                                val config = loader.load()

                                val servers = config.node("servers").childrenMap().map { it.key.toString() to it.value }.toMap().toMutableMap()
                                servers.remove(target.name)
                                config.node("servers").set(servers)
                                loader.save(config)

                                serverConfig.servers.removeIf { it.name.equals(name, true) }
                                VelocityMan10Manager.reloadServers()

                                sender.sendRichMessage(commandConfig.serverRemoved.replace("%name%", name))
                                return@executes Command.SINGLE_SUCCESS
                            }
                    )
            )
        return BrigadierCommand(node)
    }
}