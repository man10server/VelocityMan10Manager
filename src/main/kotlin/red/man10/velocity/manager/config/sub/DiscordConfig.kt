package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class DiscordConfig: AbstractConfig() {
    override val internalName = "discord"

    var token = ""
    var guildId: Long = 0

    var chatChannelId: Long = 0
    var systemChannelId: Long = 0
    var notificationChannelId: Long = 0
    var logChannelId: Long = 0
    var adminChannelId: Long = 0
    var reportChannelId: Long = 0
    var jailChannelId: Long = 0

    override fun loadConfig(config: CommentedConfigurationNode) {
        token = config.node("token").getString("")
        guildId = config.node("guildId").getLong(0)
        val channelsNode = config.node("channels")
        chatChannelId = channelsNode.node("chat").getLong(0)
        systemChannelId = channelsNode.node("system").getLong(0)
        notificationChannelId = channelsNode.node("notification").getLong(0)
        logChannelId = channelsNode.node("log").getLong(0)
        adminChannelId = channelsNode.node("admin").getLong(0)
        reportChannelId = channelsNode.node("report").getLong(0)
        jailChannelId = channelsNode.node("jail").getLong(0)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("token").set("")
        config.node("guildId").set(0)
        val channelsNode = config.node("channels")
        channelsNode.node("chat").set(0)
        channelsNode.node("system").set(0)
        channelsNode.node("notification").set(0)
        channelsNode.node("log").set(0)
        channelsNode.node("admin").set(0)
        channelsNode.node("report").set(0)
        channelsNode.node("jail").set(0)
    }
}