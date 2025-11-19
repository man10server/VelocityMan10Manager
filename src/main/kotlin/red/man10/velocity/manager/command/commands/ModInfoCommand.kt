package red.man10.velocity.manager.command.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.CommandSource
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.command.AbstractCommand
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig

class ModInfoCommand: AbstractCommand() {
    override fun getMeta(manager: CommandManager): CommandMeta {
        return manager.metaBuilder("modinfo")
            .build()
    }

    fun help(context: CommandContext<CommandSource>): Int {
        val commandConfig = Config.getOrThrow<CommandConfig>()
        context.source.sendRichMessage(commandConfig.modInfoHelpMessage)
        return Command.SINGLE_SUCCESS
    }

    override fun createCommand(): BrigadierCommand {
        val node = BrigadierCommand.literalArgumentBuilder("modinfo")
            .requires { sender -> sender.hasPermission("red.man10.velocity.command.modinfo") }
            .executes(this::help)
            .then(
                BrigadierCommand.requiredArgumentBuilder("対象", StringArgumentType.word())
                    .suggests { _, builder ->
                        playerSuggestions(builder)
                    }
                    .executes { context ->
                        val config = Config.getOrThrow<CommandConfig>()
                        val sender = context.source
                        val targetName = StringArgumentType.getString(context, "対象")
                        val target = VelocityMan10Manager.proxy.getPlayer(targetName).orElse(null)
                        if (target == null) {
                            sender.sendRichMessage(config.playerNotFound)
                            return@executes Command.SINGLE_SUCCESS
                        }
                        val modInfo = target.modInfo.orElse(null)
                        if (modInfo == null) {
                            sender.sendRichMessage(config.modInfoNotFound)
                            return@executes Command.SINGLE_SUCCESS
                        }

                        sender.sendRichMessage(config.modInfoModInfo.applyPlaceholders(mapOf(
                            "name" to target.username,
                            "type" to modInfo.type
                        )))

                        val builder = StringBuilder()
                        modInfo.mods.forEach { mod ->
                            builder.append(config.modInfoModInfoFormat.applyPlaceholders(
                                mapOf(
                                    "modid" to mod.id,
                                    "version" to mod.version
                                )
                            ))
                            builder.append("\n")
                        }

                        sender.sendRichMessage(builder.toString().trim())

                        Command.SINGLE_SUCCESS
                    }
            )
        return BrigadierCommand(node)
    }
}