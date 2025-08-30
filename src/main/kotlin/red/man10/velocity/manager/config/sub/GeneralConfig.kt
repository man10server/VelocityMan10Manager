package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class GeneralConfig: AbstractConfig() {
    override val internalName: String = "general"

    var prefix = "§f[§dMan§f10§aBot§f]§r"
    var enableJapanizer = false

    override fun loadConfig(config: CommentedConfigurationNode) {
        prefix = config.node("prefix").getString(prefix) ?: prefix
        enableJapanizer = config.node("enableJapanizer").getBoolean(true)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("prefix").set(prefix)
        config.node("enableJapanizer").set(true)
    }
}