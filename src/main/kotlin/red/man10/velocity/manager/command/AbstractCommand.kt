package red.man10.velocity.manager.command

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandMeta
import red.man10.velocity.manager.VelocityMan10Manager
import java.util.concurrent.CompletableFuture

abstract class AbstractCommand {

    abstract fun getMeta(manager: CommandManager): CommandMeta

    abstract fun createCommand(): BrigadierCommand

    protected fun playerSuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        VelocityMan10Manager.proxy.allPlayers
            .map { it.username }
            .filter { it.startsWith(builder.remaining, ignoreCase = true) }
            .sortedBy { it.lowercase() }
            .forEach { builder.suggest(it) }
        return builder.buildFuture()
    }
}