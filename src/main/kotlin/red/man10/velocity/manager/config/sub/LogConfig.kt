package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class LogConfig: AbstractConfig() {
    override val internalName: String = "log"

    var login = true
    var logout = true
    var chat = true
    var chatDiscord = true
    var command = true

    var loginLogFormat = "**%name% is connected**"
    var logoutLogFormat = "**%name% is disconnected**"
    var chatLogFormat = "[CHAT] <%server%> %name%: %message%"
    var chatDiscordLogFormat = "[DISCORD] %username%: %message%"
    var commandLogFormat = "[COMMAND] <%server%> %name%: %command%"

    var banLogFormat = """
        %name%は「%reason%」の理由によりBANされました！(処罰者:%punisher%)
        解除日:%date%
    """.trimIndent()
    var banReleaseLogFormat = "%name%は「%reason%」の理由によりBAN解除されました(解除者:%punisher%)"
    var msbLogFormat = "%name%は「%reason%」の理由により、MSBされました！(処罰者:%punisher%)"
    var msbReleaseLogFormat = "%name%は「%reason%」の理由により、MSB解除されました(解除者:%punisher%)"
    var warnLogFormat = "%name%は「%reason%」の理由により%score%ポイント引かれ、警告されました！(処罰者:%punisher%)"
    var muteLogFormat = """
        %name%は「%reason%」の理由によりミュートされました！(処罰者:%punisher%)
        解除日:%date%
    """.trimIndent()
    var muteReleaseLogFormat = "%name%は「%reason%」の理由によりミュート解除されました(解除者:%punisher%)"
    var jailLogFormat = """
        %name%は「%reason%」の理由により島流しにされました！(処罰者:%punisher%)
        釈放日:%date%
    """.trimIndent()
    var jailReleaseLogFormat = "%name%は「%reason%」の理由により釈放されました(解除者:%punisher%)"
    var ipBanLogFormat = "%ip%を「%reason%」の理由によりIPBANしました(処罰者:%punisher%)"
    var ipBanReleaseLogFormat = "%ip%を「%reason%」の理由によりIPBAN解除しました(解除者:%punisher%)"

    override fun loadConfig(config: CommentedConfigurationNode) {
        val enabledNode = config.node("enabled")
        login = enabledNode.node("login").getBoolean(login)
        logout = enabledNode.node("logout").getBoolean(logout)
        chat = enabledNode.node("chat").getBoolean(chat)
        chatDiscord = enabledNode.node("chatDiscord").getBoolean(chatDiscord)
        command = enabledNode.node("command").getBoolean(command)

        loginLogFormat = config.node("loginLogFormat").getString(loginLogFormat)
        logoutLogFormat = config.node("logoutLogFormat").getString(logoutLogFormat)
        chatLogFormat = config.node("chatLogFormat").getString(chatLogFormat)
        chatDiscordLogFormat = config.node("chatDiscordLogFormat").getString(chatDiscordLogFormat)
        commandLogFormat = config.node("commandLogFormat").getString(commandLogFormat)

        val punishmentNode = config.node("punishment")
        banLogFormat = punishmentNode.node("banLogFormat").getString(banLogFormat)
        banReleaseLogFormat = punishmentNode.node("banReleaseLogFormat").getString(banReleaseLogFormat)
        msbLogFormat = punishmentNode.node("msbLogFormat").getString(msbLogFormat)
        msbReleaseLogFormat = punishmentNode.node("msbReleaseLogFormat").getString(msbReleaseLogFormat)
        warnLogFormat = punishmentNode.node("warnLogFormat").getString(warnLogFormat)
        muteLogFormat = punishmentNode.node("muteLogFormat").getString(muteLogFormat)
        muteReleaseLogFormat = punishmentNode.node("muteReleaseLogFormat").getString(muteReleaseLogFormat)
        jailLogFormat = punishmentNode.node("jailLogFormat").getString(jailLogFormat)
        jailReleaseLogFormat = punishmentNode.node("jailReleaseLogFormat").getString(jailReleaseLogFormat)
        ipBanLogFormat = punishmentNode.node("ipBanLogFormat").getString(ipBanLogFormat)
        ipBanReleaseLogFormat = punishmentNode.node("ipBanReleaseLogFormat").getString(ipBanReleaseLogFormat)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        val enabledNode = config.node("enabled")
        enabledNode.node("login").set(login)
        enabledNode.node("logout").set(logout)
        enabledNode.node("chat").set(chat)
        enabledNode.node("chatDiscord").set(chatDiscord)
        enabledNode.node("command").set(command)

        config.node("loginLogFormat").set(loginLogFormat)
        config.node("logoutLogFormat").set(logoutLogFormat)
        config.node("chatLogFormat").set(chatLogFormat)
        config.node("chatDiscordLogFormat").set(chatDiscordLogFormat)
        config.node("commandLogFormat").set(commandLogFormat)

        val punishmentNode = config.node("punishment")
        punishmentNode.node("banLogFormat").set(banLogFormat)
        punishmentNode.node("banReleaseLogFormat").set(banReleaseLogFormat)
        punishmentNode.node("msbLogFormat").set(msbLogFormat)
        punishmentNode.node("msbReleaseLogFormat").set(msbReleaseLogFormat)
        punishmentNode.node("warnLogFormat").set(warnLogFormat)
        punishmentNode.node("muteLogFormat").set(muteLogFormat)
        punishmentNode.node("muteReleaseLogFormat").set(muteReleaseLogFormat)
        punishmentNode.node("jailLogFormat").set(jailLogFormat)
        punishmentNode.node("jailReleaseLogFormat").set(jailReleaseLogFormat)
        punishmentNode.node("ipBanLogFormat").set(ipBanLogFormat)
        punishmentNode.node("ipBanReleaseLogFormat").set(ipBanReleaseLogFormat)
    }
}