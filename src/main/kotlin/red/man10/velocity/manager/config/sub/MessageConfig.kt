package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class MessageConfig: AbstractConfig() {
    override val internalName: String = "message"

    var failedToConnectServerMessage = "<red><bold>サーバーに接続できませんでした しばらくしてから再度お試しください"

    var authenticationMessage = "<white><bold>この数字をチャットに入力してください => <green><bold>%code%"
    var authenticationTitle = "<gold><bold>%code%"
    var authenticationSubtitle = "<gold><bold>↑チャットに数字を入れよう！↑"
    var authenticationSuccessMessage = """
        <green><bold>認証できました！(Authentication Success!)
        <green><bold>ようこそman10サーバーへ！
        <green><bold>5秒後にロビーにテレポートします！
    """.trimIndent()

    var msbMessage = ""

    var banMessage = """
        <dark_red><bold>You are banned. : あなたはこのサーバーからBanされています
        <green>身に覚えがない場合は、Man10公式Discordの#reportにお申し出ください。
        If you do not remember it, please report it to #report on the official Man10 Discord.
    """.trimIndent()
    var banBroadcastMessage = """
        <red><bold>%name%は「%reason%」の理由により、1000ポイント引かれ、BANされました！
        <red><bold>解除日:%date%
    """.trimIndent()
    var banBroadcastDiscordMessage = """
        **%name%は「%reason%」の理由により、1000ポイント引かれ、BANされました！**
        **解除日:%date%**
    """.trimIndent()
    var banReleaseMessage = "<red><bold>%name%はBAN解除されました"
    var banReleaseDiscordMessage = "**%name%のBANが解除されました**"
    var warnMessage = "<red><bold>あなたは「%reason%」の理由により、%score%ポイント引かれ、警告されました！"
    var warnBroadcastMessage = "<red>%name%は「%reason%」の理由により%score%ポイント引かれ、警告されました！"
    var warnBroadcastDiscordMessage = "**%name%は「%reason%」の理由により%score%ポイント引かれ、警告されました！**"
    var muteMessage = "<yellow>あなたはミュートされています！"
    var muteReleaseMessage = "<red><bold>%name%はミュート解除されました"
    var muteReleaseDiscordMessage = "**%name%はミュート解除されました**"
    var muteBroadcastMessage = """
        <red><bold>%name%は「%reason%」の理由により、ミュートされました！
        <red><bold>解除日:%date%
    """.trimIndent()
    var muteBroadcastDiscordMessage = """
        **%name%は「%reason%」の理由により、ミュートされました！**
        **解除日:%date%**
    """.trimIndent()
    var jailMessage = "<red><bold>あなたは「%reason%」により、現在島にいます！"
    var jailedMessage = "<red><bold>あなたは「%reason%」の理由により、300ポイント引かれ、島流しにされました！"
    var jailOnCommandMessage = "<yellow>あなたは島流しにあっています！"
    var jailTitle = "<red><bold>あなたは島にいます"
    var jailSubtitle = ""
    var jailBroadcastMessage = """
        <red><bold>%name%は「%reason%」の理由により、300ポイント引かれ、Jailされました！
        <red><bold>釈放日:%date%
    """.trimIndent()
    var jailBroadcastDiscordMessage = """
        **%name%は「%reason%」の理由により、300ポイント引かれ、Jailされました！**
        **釈放日:%date%**
    """.trimIndent()
    var jailReleaseMessage = "<red<bold>%name%は釈放されました"
    var jailReleaseDiscordMessage = "**%name%は釈放されました**"

    // スコアに応じたログイン・ログアウトメッセージ
    // %score% スコア
    // %name% ユーザー名
    var minecraftLoginMessages = mutableMapOf<IntRange, String>()
    var minecraftLogoutMessages = mutableMapOf<IntRange, String>()

    var discordLoginMessage = "**%name%がログインしました**"
    var discordLogoutMessage = "**%name%がログアウトしました**"

    var minecraftFirstLoginMessage = "<aqua><bold>%name%<yellow><bold>さんがMan10サーバーに初参加しました！ <aqua><bold>%count%<yellow><bold>人目のプレイヤーです！"
    var discordFirstLoginMessage = "**%name%**さんがMan10サーバーに初参加しました！ **%count%**人目のプレイヤーです！"

    var serverBootMessage = ":ballot_box_with_check:**サーバーが起動しました**"
    var serverShutdownMessage = ":octagonal_sign:**サーバーがシャットダウンしました**"

    var alertMessageFormat = "<dark_gray>[<dark_red>Alert<dark_gray>]<white> %message%"

    override fun loadConfig(config: CommentedConfigurationNode) {
        failedToConnectServerMessage = config.node("failedToConnectServerMessage").getString(failedToConnectServerMessage)

        val authNode = config.node("authentication")
        authenticationMessage = authNode.node("message").getString(authenticationMessage)
        authenticationTitle = authNode.node("title").getString(authenticationTitle)
        authenticationSubtitle = authNode.node("subtitle").getString(authenticationSubtitle)

        val punishNode = config.node("punishment")

        msbMessage = punishNode.node("msbMessage").getString(msbMessage)

        val banNode = punishNode.node("ban")
        banMessage = banNode.node("message").getString(banMessage)
        banBroadcastMessage = banNode.node("broadcastMessage").getString(banBroadcastMessage)
        banBroadcastDiscordMessage = banNode.node("broadcastDiscordMessage").getString(banBroadcastDiscordMessage)
        banReleaseMessage = banNode.node("releaseMessage").getString(banReleaseMessage)
        banReleaseDiscordMessage = banNode.node("releaseDiscordMessage").getString(banReleaseDiscordMessage)

        val warnNode = punishNode.node("warn")
        warnMessage = warnNode.node("message").getString(warnMessage)
        warnBroadcastMessage = warnNode.node("broadcastMessage").getString(warnBroadcastMessage)
        warnBroadcastDiscordMessage = warnNode.node("broadcastDiscordMessage").getString(warnBroadcastDiscordMessage)

        val muteNode = punishNode.node("mute")
        muteMessage = muteNode.node("message").getString(muteMessage)
        muteReleaseMessage = muteNode.node("releaseMessage").getString(muteReleaseMessage)
        muteReleaseDiscordMessage = muteNode.node("releaseDiscordMessage").getString(muteReleaseDiscordMessage)
        muteBroadcastMessage = muteNode.node("broadcastMessage").getString(muteBroadcastMessage)
        muteBroadcastDiscordMessage = muteNode.node("broadcastDiscordMessage").getString(muteBroadcastDiscordMessage)

        val jailNode = punishNode.node("jail")
        jailMessage = jailNode.node("message").getString(jailMessage)
        jailOnCommandMessage = jailNode.node("onCommandMessage").getString(jailOnCommandMessage)
        jailTitle = jailNode.node("title").getString(jailTitle)
        jailSubtitle = jailNode.node("subtitle").getString(jailSubtitle)
        jailBroadcastMessage = jailNode.node("broadcastMessage").getString(jailBroadcastMessage)
        jailBroadcastDiscordMessage = jailNode.node("broadcastDiscordMessage").getString(jailBroadcastDiscordMessage)
        jailReleaseMessage = jailNode.node("releaseMessage").getString(jailReleaseMessage)
        jailReleaseDiscordMessage = jailNode.node("releaseDiscordMessage").getString(jailReleaseDiscordMessage)

        val loginMessages = config.node("minecraftLoginMessages").childrenMap()
        minecraftLoginMessages.clear()
        loginMessages.forEach { (key, value) ->
            val range = key.toString().split("..").mapNotNull { it.toIntOrNull() }
            if (range.size == 2) {
                minecraftLoginMessages[range[0]..range[1]] = value.getString("")
            }
        }

        val logoutMessages = config.node("minecraftLogoutMessages").childrenMap()
        minecraftLogoutMessages.clear()
        logoutMessages.forEach { (key, value) ->
            val range = key.toString().split("..").mapNotNull { it.toIntOrNull() }
            if (range.size == 2) {
                minecraftLogoutMessages[range[0]..range[1]] = value.getString("")
            }
        }

        discordLoginMessage = config.node("discordLoginMessage").getString(discordLoginMessage)
        discordLogoutMessage = config.node("discordLogoutMessage").getString(discordLogoutMessage)

        val firstLoginMessageNode = config.node("firstLoginMessage")
        minecraftFirstLoginMessage = firstLoginMessageNode.node("minecraft").getString(minecraftFirstLoginMessage)
        discordFirstLoginMessage = firstLoginMessageNode.node("discord").getString(discordFirstLoginMessage)

        serverBootMessage = config.node("serverBootMessage").getString(serverBootMessage)
        serverShutdownMessage = config.node("serverShutdownMessage").getString(serverShutdownMessage)

        alertMessageFormat = config.node("alertMessageFormat").getString(alertMessageFormat)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("failedToConnectServerMessage").set(failedToConnectServerMessage)

        val authNode = config.node("authentication")
        authNode.node("message").set(authenticationMessage)
        authNode.node("title").set(authenticationTitle)
        authNode.node("subtitle").set(authenticationSubtitle)

        val punishNode = config.node("punishment")

        punishNode.node("msbMessage").set(msbMessage)

        val banNode = punishNode.node("ban")
        banNode.node("message").set(banMessage)
        banNode.node("broadcastMessage").set(banBroadcastMessage)
        banNode.node("broadcastDiscordMessage").set(banBroadcastDiscordMessage)
        banNode.node("releaseMessage").set(banReleaseMessage)
        banNode.node("releaseDiscordMessage").set(banReleaseDiscordMessage)

        val warnNode = punishNode.node("warn")
        warnNode.node("message").set(warnMessage)
        warnNode.node("broadcastMessage").set(warnBroadcastMessage)
        warnNode.node("broadcastDiscordMessage").set(warnBroadcastDiscordMessage)

        val muteNode = punishNode.node("mute")
        muteNode.node("message").set(muteMessage)
        muteNode.node("releaseMessage").set(muteReleaseMessage)
        muteNode.node("releaseDiscordMessage").set(muteReleaseDiscordMessage)
        muteNode.node("broadcastMessage").set(muteBroadcastMessage)
        muteNode.node("broadcastDiscordMessage").set(muteBroadcastDiscordMessage)

        val jailNode = punishNode.node("jail")
        jailNode.node("message").set(jailMessage)
        jailNode.node("onCommandMessage").set(jailOnCommandMessage)
        jailNode.node("title").set(jailTitle)
        jailNode.node("subtitle").set(jailSubtitle)
        jailNode.node("broadcastMessage").set(jailBroadcastMessage)
        jailNode.node("broadcastDiscordMessage").set(jailBroadcastDiscordMessage)
        jailNode.node("releaseMessage").set(jailReleaseMessage)
        jailNode.node("releaseDiscordMessage").set(jailReleaseDiscordMessage)

        val minecraftLoginMessagesNode = config.node("minecraftLoginMessages")
        minecraftLoginMessagesNode.node("0..10000").set("<yellow>%name%がMan10Networkにログインしました スコア:%score%ポイント")

        val minecraftLogoutMessagesNode = config.node("minecraftLogoutMessages")
        minecraftLogoutMessagesNode.node("0..10000").set("<yellow>%name%がMan10Networkからログアウトしました")

        val firstLoginMessageNode = config.node("firstLoginMessage")
        firstLoginMessageNode.node("minecraft").set(minecraftFirstLoginMessage)
        firstLoginMessageNode.node("discord").set(discordFirstLoginMessage)

        config.node("discordLoginMessage").set(discordLoginMessage)
        config.node("discordLogoutMessage").set(discordLogoutMessage)

        config.node("serverBootMessage").set(serverBootMessage)
        config.node("serverShutdownMessage").set(serverShutdownMessage)

        config.node("alertMessageFormat").set(alertMessageFormat)
    }
}