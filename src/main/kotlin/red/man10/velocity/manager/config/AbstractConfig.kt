package red.man10.velocity.manager.config

import org.spongepowered.configurate.CommentedConfigurationNode

abstract class AbstractConfig {

    abstract val internalName: String

    abstract fun loadConfig(config: CommentedConfigurationNode)

    abstract fun saveDefaultConfig(config: CommentedConfigurationNode)
}