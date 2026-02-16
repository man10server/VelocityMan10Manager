package red.man10.velocity.manager.config.sub

import org.spongepowered.configurate.CommentedConfigurationNode
import red.man10.velocity.manager.command.PunishmentCommand
import red.man10.velocity.manager.config.AbstractConfig

class PunishmentConfig: AbstractConfig() {
    override val internalName: String = "punishment"

    var announceBan = false
    var announceAltBan = false
    var announceMute = false
    var announceJail = false

    val presetPunishments = mutableMapOf<String, Pair<Long, String>>()
    //key:プリセット名 value:Pair<期間(分),理由>

    override fun loadConfig(config: CommentedConfigurationNode) {
        val announceNode = config.node("announce")
        announceBan = announceNode.node("ban").getBoolean(false)
        announceAltBan = announceNode.node("altBan").getBoolean(false)
        announceMute = announceNode.node("mute").getBoolean(false)
        announceJail = announceNode.node("jail").getBoolean(false)

        val presetsNode = config.node("presetPunishments").childrenMap()
        presetPunishments.clear()
        presetsNode?.forEach { (key, value) ->
            val presetName = key.toString()
            val duration = PunishmentCommand.parseDuration(value.node("duration").getString(""))
            val reason = value.node("reason").getString("")

            if (duration != PunishmentCommand.INVALID && !reason.isNullOrEmpty()) {
                presetPunishments[presetName] = Pair(duration, reason)
            }
        }
    }

    override fun saveDefaultConfig(config: CommentedConfigurationNode) {
        val announceNode = config.node("announce")
            .comment("""
                |# 処罰時に全体チャットで通知するかどうか
            """.trimIndent())
        announceNode.node("ban").set(false)
        announceNode.node("altBan").set(false)
        announceNode.node("warn").set(false)
        announceNode.node("mute").set(false)
        announceNode.node("jail").set(false)

        config.node("presetPunishments")
            .comment("""
                |# プリセット名をキーとして、期間と理由を設定します。
                |# 期間は(m,h,d,0k)の形式で指定します。0kを指定すると無期限になります。
                |# 例:
                |# preset1:
                |#   duration: 1d
                |#   reason: 迷惑行為
                |# preset2:
                |#   duration: 0k
                |#   reason: ハック行為
            """.trimIndent())
            .set(mapOf<String, Any>())
    }


}