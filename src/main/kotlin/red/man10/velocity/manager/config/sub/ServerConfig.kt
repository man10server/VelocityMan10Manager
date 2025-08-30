package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class ServerConfig: AbstractConfig() {
    override val internalName: String = "server"

    var login = "login"
    var man10 = "man10"
    var jail = "jail"

    override fun loadConfig(config: CommentedConfigurationNode) {
        login = config.node("login").getString(login)
        man10 = config.node("man10").getString(man10)
        jail = config.node("jail").getString(jail)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("login").set("login")
        config.node("man10").set("man10")
        config.node("jail").set("jail")
    }
}