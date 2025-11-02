package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class CommandConfig: AbstractConfig() {
    override val internalName: String = "command"

    var cannotUseInConsole = "<red><bold>このコマンドはコンソールから実行できません"
    var playerNotFound = "<dark_red>存在しないユーザーです"
    var errorOccurred = "<red>エラーが発生しました"

    var helpMessage = """
        <green> ============ Man10VelocityManager ============
        <light_purple>/mvelocity help : このヘルプを表示します
        <light_purple>/mvelocity reload : 全ての機能をリロードします
        <red>以下のコマンドは/mvelocity無しでも実行できます
        <light_purple>/mvelocity report : reportのヘルプを表示します
        <light_purple>/mvelocity (tell, msg, message, m, w, t) : tellのヘルプを表示します
        <light_purple>/mvelocity (reply, r) : replyのヘルプを表示します
        <light_purple>/mvelocity mchat : チャット設定のヘルプを表示します
        <light_purple>/mvelocity mban : バンのヘルプを表示します
        <light_purple>/mvelocity msb : シャドウバンのヘルプを表示します
        <light_purple>/mvelocity mwarn : 警告のヘルプを表示します
        <light_purple>/mvelocity mjail : Jailのヘルプを表示します
        <light_purple>/mvelocity mmute : ミュートのヘルプを表示します
        <light_purple>/mvelocity malt : サブアカウント関連のヘルプを表示します
        <light_purple>/mvelocity mserver : サーバー関連のヘルプを表示します
        <green> ================================================
        
    """.trimIndent()
    var reloaded = "<green><bold>Man10VelocityManagerの全ての機能をリロードしました"

    var reportHelpMessage = """
        <green><bold>〜サーバーへ報告をする〜
        <green>/report <タイトル> <本文>
        <light_purple>不具合の報告や、荒らしをしていた場合に報告をしてください。
        <light_purple>いたずら目的で使ってはいけません。
        <light_purple>うっかり途中で送信してしまった場合は、
        <light_purple>同じタイトルで、続きを書いてください。
        <light_purple>レポートの内容は、即座にサーバー運営が見れるチャンネルに転送されます。
    """.trimIndent()
    var reportSameContent = "<red><bold>同じ内容を複数回レポートすることはできません"
    var reportSend = """
        <white><bold>送信したタイトル:%title%
        <white><bold>送信した内容:%content%
        <green><bold>送信しました！ご協力ありがとうございます！
    """.trimIndent()

    var privateChatAdminTag = "(GM)"
    var privateChatFormat = "<aqua>個人チャット[%sender%@%sender_server%%sender_admin_tag% > %receiver%@%receiver_server%%receiver_admin_tag%] <white>%message%"
    var privateChatPlayerNotFound = "<red>メッセージ送信先が見つかりません。 The destination for the message was not found."
    var tellHelpMessage = "<red>/%command% <player> <message> : プライベートメッセージを送ります。 Send private message."
    var tellCannotSelf = "<red>自分自身にはプライベートメッセージを送信することができません。 Cannot send a private message to myself."
    var replyCurrentlyNoOne = "<red>現在の会話相手はいません。 There is no current conversation partner."
    var replyCurrentlyPlayer = "<light_purple>現在の会話相手:%name% <light_purple>Current Conversation Partner: %name%"

    var chatHelpMessage = """
        <light_purple><bold>/mchat <server> cancelSend <true|false> : このサーバーから他のサーバーにチャットを送るのを有効/無効にします
        <light_purple><bold>/mchat <server> cancelReceive <true|false> : このサーバーで他のサーバーからのチャットを受け取るのを有効/無効にします
    """.trimIndent()
    var chatServerNotFound = "<red><bold>サーバーが見つかりません"
    var chatToggleNotBoolean = "<red><bold>引数はtrueかfalseで指定してください"
    var chatCancelSendToggleTrue = "<green><bold>このサーバーから他のサーバーにチャットが送れないようになりました"
    var chatCancelSendAlreadyTrue = "<red><bold>すでにこのサーバーから他のサーバーにチャットが送れないようになっています"
    var chatCancelSendToggleFalse = "<green><bold>このサーバーから他のサーバーにチャットが送れるようになりました"
    var chatCancelSendAlreadyFalse = "<red><bold>すでにこのサーバーから他のサーバーにチャットが送れるようになっています"
    var chatCancelReceiveToggleTrue = "<green><bold>このサーバーで他のサーバーからのチャットを受け取らないようになりました"
    var chatCancelReceiveAlreadyTrue = "<red><bold>すでにこのサーバーで他のサーバーからのチャットを受け取らないようになっています"
    var chatCancelReceiveToggleFalse = "<green><bold>このサーバーで他のサーバーからのチャットを受け取るようになりました"
    var chatCancelReceiveAlreadyFalse = "<red><bold>すでにこのサーバーで他のサーバーからのチャットを受け取るようになっています"

    var punishmentInvalidDuration = "<red><bold>時間の指定方法が不適切です"
    var punishmentInvalidReason = "<red><bold>理由が指定されていません"
    var punishmentPresetNotFound = "<red><bold>プリセットが見つかりません"
    var banHelpMessage = """
        <light_purple><bold>/mban <player> <期間(d/h/m/0k/reset)> <理由>
        <light_purple><bold>/mban <player> preset <プリセット名>
    """.trimIndent()
    var banAlreadyReleased = "<red><bold>このユーザーは既にBAN解除されています！"
    var msbHelpMessage = """
        <light_purple><bold>/msb <player> <期間(d/h/m/0k/reset)> <理由>
        <light_purple><bold>/msb <player> preset <プリセット名>
    """.trimIndent()
    var msbAlreadyReleased = "<red><bold>このユーザーは既にMSB解除されています！"
    var msbBanned = "<red><bold>%name%を「%reason%」の理由でBANしました"
    var msbRelease = "<red><bold>%name%のMSBを解除しました"
    var warnHelpMessage = """
        <light_purple><bold>/mwarn <player> <減らすスコア> <理由>
    """.trimIndent()
    var jailHelpMessage = """
        <light_purple><bold>/mjail <player> <期間(d/h/m/0k/reset)> <理由>
        <light_purple><bold>/mjail <player> preset <プリセット名>
    """.trimIndent()
    var jailAlreadyReleased = "<red><bold>このユーザーは既に釈放されています！"
    var muteHelpMessage = """
        <light_purple><bold>/mmute <player> <期間(d/h/m/0k/reset)> <理由>
        <light_purple><bold>/mmute <player> preset <プリセット名>
    """.trimIndent()
    var muteAlreadyReleased = "<red><bold>このユーザーは既にミュート解除されています！"
    var altHelpMessage = """
        <light_purple><bold>/malt sub <player> : サブアカウントの可能性があるプレイヤーを検索します
        <light_purple><bold>/malt user <player> : 過去のIPアドレスなどを検索します
        <light_purple><bold>/malt ban <player> <理由> : 指定したプレイヤーとそのサブアカウントをmbanします
        <light_purple><bold>/malt ipban <ip> <理由> : 指定したIPアドレスをIPBANします
        <light_purple><bold>/malt releaseIpBan <ip> : 指定したIPアドレスのIPBANを解除します
    """.trimIndent()
    var altSubAccount = """
        <light_purple><bold>検索ユーザー:%name%
        <light_purple><bold>サブアカウントの可能性があるプレイヤー
    """.trimIndent()
    var altSubAccountFormat = "<red><bold>%name%"
    var altSubNotFound = "<light_purple><bold>サブアカウントの可能性があるプレイヤーが見つかりませんでした"
    var altUserSearch = """
        <light_purple><bold>検索ユーザー:%name%
        <light_purple><bold>検索ユーザーから過去のIPなどを検索
        <light_purple><bold>Name / IP / 接続回数
    """.trimIndent()
    var altUserSearchFormat = "<red><bold>%name% / %ip% / %count%"
    var altUserNotFound = "<red><bold>検索ユーザーから過去のIPなどが見つかりませんでした"
    var altIpBanPlayerOrIpNotFound = "<red><bold>指定されたユーザーまたはIPアドレスが見つかりません"
    var altIpBanned = "<red><bold>IP:%ip%を、「%reason%」の理由でBANしました"
    var altIpBanRelease = "<red><bold>IP:%ip%のIPBANを解除しました"
    var altIpBanReleaseNotFound = "<red><bold>指定されたIPアドレスはIPBANされていません"

    var serverHelpMessage = """
        <light_purple><bold>/mserver add <server> <address> <port> : サーバーを追加します
        <light_purple><bold>/mserver remove <server> : サーバーを削除します
        <light_purple><bold>/mserver list : サーバー一覧を表示します
    """.trimIndent()
    var serverIsEmpty = "<red><bold>サーバーが一つも登録されていません"
    var serverList = """
        <aqua><bold>登録されているサーバー一覧
        <aqua><bold>サーバー名 / IPアドレス / ポート
    """.trimIndent()
    var serverListFormat = "<white><bold>%name% / %address% / %port%"
    var serverAlreadyExists = "<red><bold>そのサーバー名は既に登録されています"
    var serverAdded = "<green><bold>サーバーを追加しました"
    var serverNotFound = "<red><bold>そのサーバーは登録されていません"
    var serverRemoved = "<green><bold>サーバーを削除しました"

    override fun loadConfig(config: CommentedConfigurationNode) {
        cannotUseInConsole = config.node("cannotUseInConsole").getString(cannotUseInConsole)
        playerNotFound = config.node("playerNotFound").getString(playerNotFound)
        errorOccurred = config.node("errorOccurred").getString(errorOccurred)

        helpMessage = config.node("helpMessage").getString(helpMessage)

        val reportNode = config.node("report")
        reportHelpMessage = reportNode.node("helpMessage").getString(reportHelpMessage)
        reportSameContent = reportNode.node("sameContent").getString(reportSameContent)
        reportSend = reportNode.node("send").getString(reportSend)

        val privateChatNode = config.node("privateChat")
        privateChatAdminTag = privateChatNode.node("adminTag").getString(privateChatAdminTag)
        privateChatFormat = privateChatNode.node("format").getString(privateChatFormat)
        privateChatPlayerNotFound = privateChatNode.node("playerNotFound").getString(privateChatPlayerNotFound)
        val tellNode = privateChatNode.node("tell")
        tellHelpMessage = tellNode.node("helpMessage").getString(tellHelpMessage)
        tellCannotSelf = tellNode.node("cannotSelf").getString(tellCannotSelf)
        val replyNode = privateChatNode.node("reply")
        replyCurrentlyNoOne = replyNode.node("currentlyNoOne").getString(replyCurrentlyNoOne)
        replyCurrentlyPlayer = replyNode.node("currentlyPlayer").getString(replyCurrentlyPlayer)

        val chat = config.node("chat")
        chatHelpMessage = chat.node("helpMessage").getString(chatHelpMessage)
        chatServerNotFound = chat.node("serverNotFound").getString(chatServerNotFound)
        chatToggleNotBoolean = chat.node("toggleNotBoolean").getString(chatToggleNotBoolean)
        val chatCancelSendNode = chat.node("cancelSend")
        chatCancelSendToggleTrue = chatCancelSendNode.node("toggleTrue").getString(chatCancelSendToggleTrue)
        chatCancelSendAlreadyTrue = chatCancelSendNode.node("alreadyTrue").getString(chatCancelSendAlreadyTrue)
        chatCancelSendToggleFalse = chatCancelSendNode.node("toggleFalse").getString(chatCancelSendToggleFalse)
        chatCancelSendAlreadyFalse = chatCancelSendNode.node("alreadyFalse").getString(chatCancelSendAlreadyFalse)
        val chatCancelReceiveNode = chat.node("cancelReceive")
        chatCancelReceiveToggleTrue = chatCancelReceiveNode.node("toggleTrue").getString(chatCancelReceiveToggleTrue)
        chatCancelReceiveAlreadyTrue = chatCancelReceiveNode.node("alreadyTrue").getString(chatCancelReceiveAlreadyTrue)
        chatCancelReceiveToggleFalse = chatCancelReceiveNode.node("toggleFalse").getString(chatCancelReceiveToggleFalse)
        chatCancelReceiveAlreadyFalse = chatCancelReceiveNode.node("alreadyFalse").getString(chatCancelReceiveAlreadyFalse)

        val punishmentNode = config.node("punishment")
        punishmentInvalidDuration = punishmentNode.node("invalidDuration").getString(punishmentInvalidDuration)
        punishmentInvalidReason = punishmentNode.node("invalidReason").getString(punishmentInvalidReason)
        punishmentPresetNotFound = punishmentNode.node("presetNotFound").getString(punishmentPresetNotFound)

        val banNode = punishmentNode.node("ban")
        banHelpMessage = banNode.node("banHelpMessage").getString(banHelpMessage)
        banAlreadyReleased = banNode.node("alreadyReleased").getString(banAlreadyReleased)

        val msbNode = punishmentNode.node("msb")
        msbHelpMessage = msbNode.node("msbHelpMessage").getString(msbHelpMessage)
        msbAlreadyReleased = msbNode.node("alreadyReleased").getString(msbAlreadyReleased)
        msbBanned = msbNode.node("msbBanned").getString(msbBanned)
        msbRelease = msbNode.node("msbRelease").getString(msbRelease)

        val warnNode = punishmentNode.node("warn")
        warnHelpMessage = warnNode.node("warnHelpMessage").getString(warnHelpMessage)

        val jailNode = punishmentNode.node("jail")
        jailHelpMessage = jailNode.node("jailHelpMessage").getString(jailHelpMessage)
        jailAlreadyReleased = jailNode.node("alreadyReleased").getString(jailAlreadyReleased)

        val muteNode = punishmentNode.node("mute")
        muteHelpMessage = muteNode.node("muteHelpMessage").getString(muteHelpMessage)
        muteAlreadyReleased = muteNode.node("alreadyReleased").getString(muteAlreadyReleased)

        val altNode = config.node("alt")
        altHelpMessage = altNode.node("altHelpMessage").getString(altHelpMessage)
        altSubAccount = altNode.node("subAccount").getString(altSubAccount)
        altSubAccountFormat = altNode.node("subAccountFormat").getString(altSubAccountFormat)
        altSubNotFound = altNode.node("subNotFound").getString(altSubNotFound)
        altUserSearch = altNode.node("userSearch").getString(altUserSearch)
        altUserSearchFormat = altNode.node("userSearchFormat").getString(altUserSearchFormat)
        altUserNotFound = altNode.node("userNotFound").getString(altUserNotFound)
        altIpBanPlayerOrIpNotFound = altNode.node("ipBanPlayerOrIpNotFound").getString(altIpBanPlayerOrIpNotFound)
        altIpBanned = altNode.node("ipBanned").getString(altIpBanned)
        altIpBanRelease = altNode.node("ipBanRelease").getString(altIpBanRelease)
        altIpBanReleaseNotFound = altNode.node("ipBanReleaseNotFound").getString(altIpBanReleaseNotFound)

        val serverNode = config.node("server")
        serverHelpMessage = serverNode.node("serverHelpMessage").getString(serverHelpMessage)
        serverIsEmpty = serverNode.node("serverIsEmpty").getString(serverIsEmpty)
        serverList = serverNode.node("serverList").getString(serverList)
        serverListFormat = serverNode.node("serverListFormat").getString(serverListFormat)
        serverAlreadyExists = serverNode.node("serverAlreadyExists").getString(serverAlreadyExists)
        serverAdded = serverNode.node("serverAdded").getString(serverAdded)
        serverNotFound = serverNode.node("serverNotFound").getString(serverNotFound)
        serverRemoved = serverNode.node("serverRemoved").getString(serverRemoved)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("cannotUseInConsole").set(cannotUseInConsole)
        config.node("playerNotFound").set(playerNotFound)
        config.node("errorOccurred").set(errorOccurred)

        config.node("helpMessage").set(helpMessage)

        val reportNode = config.node("report")
        reportNode.node("helpMessage").set(reportHelpMessage)
        reportNode.node("sameContent").set(reportSameContent)
        reportNode.node("send").set(reportSend)

        val privateChatNode = config.node("privateChat")
        privateChatNode.node("adminTag").set(privateChatAdminTag)
        privateChatNode.node("format").set(privateChatFormat)
        privateChatNode.node("playerNotFound").set(privateChatPlayerNotFound)
        val tellNode = privateChatNode.node("tell")
        tellNode.node("helpMessage").set(tellHelpMessage)
        tellNode.node("cannotSelf").set(tellCannotSelf)
        val replyNode = privateChatNode.node("reply")
        replyNode.node("currentlyNoOne").set(replyCurrentlyNoOne)
        replyNode.node("currentlyPlayer").set(replyCurrentlyPlayer)

        val chat = config.node("chat")
        chat.node("helpMessage").set(chatHelpMessage)
        chat.node("serverNotFound").set(chatServerNotFound)
        chat.node("toggleNotBoolean").set(chatToggleNotBoolean)
        val chatCancelSendNode = chat.node("cancelSend")
        chatCancelSendNode.node("toggleTrue").set(chatCancelSendToggleTrue)
        chatCancelSendNode.node("alreadyTrue").set(chatCancelSendAlreadyTrue)
        chatCancelSendNode.node("toggleFalse").set(chatCancelSendToggleFalse)
        chatCancelSendNode.node("alreadyFalse").set(chatCancelSendAlreadyFalse)
        val chatCancelReceiveNode = chat.node("cancelReceive")
        chatCancelReceiveNode.node("toggleTrue").set(chatCancelReceiveToggleTrue)
        chatCancelReceiveNode.node("alreadyTrue").set(chatCancelReceiveAlreadyTrue)
        chatCancelReceiveNode.node("toggleFalse").set(chatCancelReceiveToggleFalse)
        chatCancelReceiveNode.node("alreadyFalse").set(chatCancelReceiveAlreadyFalse)

        val punishmentNode = config.node("punishment")
        punishmentNode.node("invalidDuration").set(punishmentInvalidDuration)
        punishmentNode.node("invalidReason").set(punishmentInvalidReason)
        punishmentNode.node("presetNotFound").set(punishmentPresetNotFound)

        val banNode = punishmentNode.node("ban")
        banNode.node("banHelpMessage").set(banHelpMessage)
        banNode.node("alreadyReleased").set(banAlreadyReleased)

        val msbNode = punishmentNode.node("msb")
        msbNode.node("msbHelpMessage").set(msbHelpMessage)
        msbNode.node("alreadyReleased").set(msbAlreadyReleased)
        msbNode.node("msbBanned").set(msbBanned)
        msbNode.node("msbRelease").set(msbRelease)

        val warnNode = punishmentNode.node("warn")
        warnNode.node("warnHelpMessage").set(warnHelpMessage)

        val jailNode = punishmentNode.node("jail")
        jailNode.node("jailHelpMessage").set(jailHelpMessage)
        jailNode.node("alreadyReleased").set(jailAlreadyReleased)

        val muteNode = punishmentNode.node("mute")
        muteNode.node("muteHelpMessage").set(muteHelpMessage)
        muteNode.node("alreadyReleased").set(muteAlreadyReleased)

        val altNode = config.node("alt")
        altNode.node("altHelpMessage").set(altHelpMessage)
        altNode.node("subAccount").set(altSubAccount)
        altNode.node("subAccountFormat").set(altSubAccountFormat)
        altNode.node("subNotFound").set(altSubNotFound)
        altNode.node("userSearch").set(altUserSearch)
        altNode.node("userSearchFormat").set(altUserSearchFormat)
        altNode.node("userNotFound").set(altUserNotFound)
        altNode.node("ipBanPlayerOrIpNotFound").set(altIpBanPlayerOrIpNotFound)
        altNode.node("ipBanned").set(altIpBanned)
        altNode.node("ipBanRelease").set(altIpBanRelease)
        altNode.node("ipBanReleaseNotFound").set(altIpBanReleaseNotFound)

        val serverNode = config.node("server")
        serverNode.node("serverHelpMessage").set(serverHelpMessage)
        serverNode.node("serverIsEmpty").set(serverIsEmpty)
        serverNode.node("serverList").set(serverList)
        serverNode.node("serverListFormat").set(serverListFormat)
        serverNode.node("serverAlreadyExists").set(serverAlreadyExists)
        serverNode.node("serverAdded").set(serverAdded)
        serverNode.node("serverNotFound").set(serverNotFound)
        serverNode.node("serverRemoved").set(serverRemoved)
    }
}