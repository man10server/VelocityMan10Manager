package red.man10.velocity.manager.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.kyori.adventure.text.minimessage.MiniMessage
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.ChatConfig
import red.man10.velocity.manager.config.sub.DiscordConfig
import red.man10.velocity.manager.config.sub.MessageConfig

object DiscordBot: ListenerAdapter() {

    lateinit var jda: JDA

    var guild: Guild? = null
    var chatChannel: TextChannel? = null
    var systemChannel: TextChannel? = null
    var logChannel: TextChannel? = null
    var adminChannel: TextChannel? = null
    var reportChannel: TextChannel? = null
    var jailChannel: TextChannel? = null

    init {
        reload()
    }

    fun reload() {
        val config = Config.getOrThrow<DiscordConfig>()
        try {
            if (::jda.isInitialized) {
                jda.shutdown()
            }
            jda = JDABuilder.createDefault(config.token)
                .enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(this)
                .build()
            jda.awaitReady()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize JDA", e)
        }

        guild = jda.getGuildById(config.guildId)
        chatChannel = guild?.getTextChannelById(config.chatChannelId)
        systemChannel = guild?.getTextChannelById(config.systemChannelId)
        logChannel = guild?.getTextChannelById(config.logChannelId)
        adminChannel = guild?.getTextChannelById(config.adminChannelId)
        reportChannel = guild?.getTextChannelById(config.reportChannelId)
        jailChannel = guild?.getTextChannelById(config.jailChannelId)

        val messageConfig = Config.getOrThrow<MessageConfig>()
        chat(messageConfig.serverBootMessage)
    }

    fun chat(message: String) {
        chatChannel?.sendMessage(message)?.queue()
    }

    fun system(message: String) {
        systemChannel?.sendMessage(message)?.queue()
    }

    fun log(message: String) {
        logChannel?.sendMessage(message)?.queue()
    }

    fun admin(message: String) {
        adminChannel?.sendMessage(message)?.queue()
    }

    fun report(message: String) {
        reportChannel?.sendMessage(message)?.queue()
    }

    fun jail(message: String) {
        jailChannel?.sendMessage(message)?.queue()
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        if (e.author == jda.selfUser) return
        if (e.channel.id != chatChannel?.id) return
        val content = e.message.contentDisplay

        val config = Config.getConfig<ChatConfig>() ?: return

        val role = e.member?.roles?.firstOrNull()
        val colorHex = role?.color?.rgb?.let {
            "<#%06X>".format(it and 0xFFFFFF)
        }

        val text = config.discordToMinecraftTextFormat
            .replace("%nickname%", e.member?.nickname ?: e.author.name)
            .replace("%username%", e.author.name)
            .replace("%role%", role?.name ?: "")
            .replace("%rolecolor%", colorHex ?: "")
        val component = MiniMessage.miniMessage()
            .deserialize(text)
            .replaceText {
                // 送信者がカラーコードなどを使えないようにする
                it.match("%message%").replacement(content)
            }

        VelocityMan10Manager.sendMessageToMinecraftPlayers(component)
    }
}