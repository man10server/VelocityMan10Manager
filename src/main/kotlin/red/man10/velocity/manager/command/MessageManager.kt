package red.man10.velocity.manager.command

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import red.man10.velocity.manager.VelocityMan10Manager
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.CommandConfig
import java.util.UUID

object MessageManager {

    val history = HashMap<UUID, Pair<UUID, String>>()

    fun putHistory(receiver: UUID, sender: Pair<UUID, String>) {
        history[receiver] = sender
    }

    fun getLastSender(receiver: UUID): Pair<UUID, String>? {
        return history[receiver]
    }

    private val consoleUUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    fun sendPrivateMessage(sender: CommandSource, receiver: Player, message: String) {
        val config = Config.getOrThrow<CommandConfig>()

        var message = message
        val japanized = VelocityMan10Manager.Companion.japanize(message)
        if (japanized != null) {
            message += " §6($japanized)"
        }

        val senderName = if (sender is Player) {
            sender.username
        } else {
            "§cConsole§r"
        }

        val senderServer = if (sender is Player) {
            sender.currentServer.map { it.serverInfo.name }.orElse("N/A")
        } else {
            "N/A"
        }

        val receiverServer = receiver.currentServer.map { it.serverInfo.name }.orElse("N/A")

        fun adminTag(source: CommandSource): String {
            return if (source.hasPermission("red.man10.velocity.admin")) {
                config.privateChatAdminTag
            } else {
                ""
            }
        }

        val formattedMessage = VelocityMan10Manager.Companion.miniMessage(
            config.privateChatFormat
                .replace("%sender%", senderName)
                .replace("%receiver%", receiver.username)
                .replace("%sender_server%", senderServer)
                .replace("%receiver_server%", receiverServer)
                .replace("%sender_admin_tag%", adminTag(sender))
                .replace("%receiver_admin_tag%", adminTag(receiver))
        ).replaceText {
            it.matchLiteral("%message%").replacement(message)
        }

        sender.sendMessage(formattedMessage)
        receiver.sendMessage(formattedMessage)

        if (sender is Player) {
            putHistory(receiver.uniqueId, Pair(sender.uniqueId, sender.username))
        } else {
            putHistory(receiver.uniqueId, Pair(consoleUUID, "§cConsole§r"))
        }
    }
}