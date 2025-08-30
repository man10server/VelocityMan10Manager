package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.config.AbstractConfig

class ChatConfig: AbstractConfig() {
    override val internalName: String = "chat"

    var cancelSendingChatServer = ArrayList<String>()
    var cancelReceivingChatServer = ArrayList<String>()

    //使えるフォーマット
    // %nickname% ニックネーム
    // %name% ユーザー名
    // %message% メッセージ内容
    // %role% 役職名(一番上の役職)
    // %rolecolor% 役職カラー(一番上の役職)
    var discordToMinecraftTextFormat = "<white>[<dark_aqua>@Discord<white>]%nickname%<aqua>:<white>%message%"

    // %server% サーバー名
    // %name% ユーザー名
    // %message% メッセージ内容
    var proxyMessageFormat = "<white>[<dark_aqua>@%server%<white>]%name%<aqua>:<white>%message%"
    var minecraftToDiscordTextFormat = "<%name%@%server%> %message%"

    override fun loadConfig(config: CommentedConfigurationNode) {
        val sendingNode = config.node("cancelSendingChatServer").childrenList()
        cancelSendingChatServer.clear()
        sendingNode?.forEach {
            val server = it.string ?: return@forEach
            cancelSendingChatServer.add(server)
        }
        val receivingNode = config.node("cancelReceivingChatServer").childrenList()
        cancelReceivingChatServer.clear()
        receivingNode?.forEach {
            val server = it.string ?: return@forEach
            cancelReceivingChatServer.add(server)
        }

        discordToMinecraftTextFormat = config.node("discordToMinecraftTextFormat").getString(discordToMinecraftTextFormat)
        proxyMessageFormat = config.node("proxyMessageFormat").getString(proxyMessageFormat)
        minecraftToDiscordTextFormat = config.node("minecraftToDiscordTextFormat").getString(minecraftToDiscordTextFormat)
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        config.node("cancelSendingChatServer").set(listOf<String>())
        config.node("cancelReceivingChatServer").set(listOf<String>())
        config.node("discordToMinecraftTextFormat").set(discordToMinecraftTextFormat)
        config.node("proxyMessageFormat").set(proxyMessageFormat)
        config.node("minecraftToDiscordTextFormat").set(minecraftToDiscordTextFormat)
    }
}