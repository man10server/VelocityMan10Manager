package red.man10.velocity.manager.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.IncomingWebhookClient
import net.dv8tion.jda.api.entities.WebhookClient
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.ErrorResponse
import net.dv8tion.jda.api.requests.GatewayIntent
import net.kyori.adventure.text.minimessage.MiniMessage
import red.man10.velocity.manager.Utils.applyPlaceholders
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.ChatConfig
import red.man10.velocity.manager.config.sub.DiscordConfig
import red.man10.velocity.manager.config.sub.LogConfig
import red.man10.velocity.manager.config.sub.MessageConfig

object DiscordBot: ListenerAdapter() {

    var jda: JDA? = null

    var guild: Guild? = null
    var chatChannel: TextChannel? = null
    var systemChannel: TextChannel? = null
    var logChannel: TextChannel? = null
    var adminChannel: TextChannel? = null
    var reportChannel: TextChannel? = null
    var jailChannel: TextChannel? = null

    var chatWebhookId: Long? = null
    var chatWebhookClient: IncomingWebhookClient? = null

    init {
        reload()
    }

    fun reload() {
        val config = Config.getOrThrow<DiscordConfig>()
        try {
            jda?.shutdown()
            jda = null

            if (!config.enabled) {
                return
            }

            val jda = JDABuilder.createDefault(config.token)
                .enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(this)
                .build()
            jda.awaitReady()
            this.jda = jda
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize JDA", e)
        }

        guild = jda?.getGuildById(config.guildId)
        chatChannel = guild?.getTextChannelById(config.chatChannelId)
        systemChannel = guild?.getTextChannelById(config.systemChannelId)
        logChannel = guild?.getTextChannelById(config.logChannelId)
        adminChannel = guild?.getTextChannelById(config.adminChannelId)
        reportChannel = guild?.getTextChannelById(config.reportChannelId)
        jailChannel = guild?.getTextChannelById(config.jailChannelId)

        setupChatWebhook(config)
    }

    private fun setupChatWebhook(config: DiscordConfig) {
        chatWebhookId = null
        chatWebhookClient = null

        val jda = jda ?: return
        val channel = chatChannel ?: return

        try {
            val webhook = channel.retrieveWebhooks().complete()
                .firstOrNull { it.name == config.chatWebhookName }
                ?: channel.createWebhook(config.chatWebhookName).complete()

            if (webhook.token == null) {
                VelocityMan10Manager.logger.error(
                    "Webhook '${config.chatWebhookName}' has no accessible token. Falling back to bot messages."
                )
                return
            }

            chatWebhookId = webhook.idLong
            chatWebhookClient = WebhookClient.createClient(jda, webhook.url)
        } catch (e: Exception) {
            VelocityMan10Manager.logger.error("Failed to set up chat webhook: ${e.message}", e)
        }
    }

    fun chat(message: String) {
        chatChannel?.sendMessage(message)?.queue()
    }

    fun chatAs(message: String, username: String, avatarUrl: String?, fallbackMessage: String) {
        val client = chatWebhookClient ?: return relayAsBot(fallbackMessage)

        try {
            client.sendMessage(message)
                .setAllowedMentions(emptyList())
                .setUsername(username)
                .setAvatarUrl(avatarUrl)
                .queue(null, ErrorHandler().handle(ErrorResponse.UNKNOWN_WEBHOOK) {
                    invalidateChatWebhook(client)
                    relayAsBot(fallbackMessage)
                })
        } catch (e: Exception) {
            VelocityMan10Manager.logger.error("Failed to send chat via webhook: ${e.message}", e)
            relayAsBot(fallbackMessage)
        }
    }

    private fun invalidateChatWebhook(stale: IncomingWebhookClient) {
        if (chatWebhookClient !== stale) return
        chatWebhookId = null
        chatWebhookClient = null
        VelocityMan10Manager.logger.error("Chat webhook is gone. Falling back to bot messages until reload.")
    }

    private fun relayAsBot(message: String) {
        chatChannel?.sendMessage(message)
            ?.setAllowedMentions(emptyList())
            ?.queue()
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
        if (e.author == jda?.selfUser) return
        if (e.author.idLong == chatWebhookId) return
        if (e.channel.id != chatChannel?.id) return
        val content = e.message.contentDisplay

        val config = Config.getOrThrow<ChatConfig>()
        val logConfig = Config.getOrThrow<LogConfig>()

        val role = e.member?.roles?.firstOrNull()
        val colorHex = role?.color?.rgb?.let {
            "<#%06X>".format(it and 0xFFFFFF)
        }

        val placeholders = mapOf(
            "nickname" to (e.member?.nickname ?: e.author.name),
            "username" to e.author.name,
            "role" to (role?.name ?: ""),
            "rolecolor" to (colorHex ?: "")
        )

        val text = config.discordToMinecraftTextFormat
            .applyPlaceholders(placeholders)
        val component = MiniMessage.miniMessage()
            .deserialize(text)
            .replaceText {
                // 送信者がカラーコードなどを使えないようにする
                it.match("%message%").replacement(content)
            }

        VelocityMan10Manager.sendMessageToMinecraftPlayers(component)
        if (logConfig.chatDiscord) {
            admin(
                logConfig.chatDiscordLogFormat.applyPlaceholders(
                    placeholders + mapOf(
                        "message" to content
                    )
                )
            )
        }
    }
}
