package red.man10.velocity.manager

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import red.man10.velocity.manager.config.Config
import red.man10.velocity.manager.config.sub.MessageConfig
import java.util.UUID
import kotlin.random.Random

object AuthProvider {

    val codeMap = HashMap<UUID, String>()

    fun showAuthenticationMessage(p: Player) {

        val config = Config.getOrThrow<MessageConfig>()

        val code = String.format("%06d", Random.nextInt(999999))
        codeMap[p.uniqueId] = code

        p.sendMessage(VelocityMan10Manager.miniMessage(
            config.authenticationMessage.replace("%code%", code)
        ))
        p.showTitle(Title.title(
            VelocityMan10Manager.miniMessage(
                config.authenticationTitle.replace("%code%", code)
            ),
            VelocityMan10Manager.miniMessage(
                config.authenticationSubtitle.replace("%code%", code)
            )
        ))
    }

    fun verifyCode(p: Player, input: String): Boolean {
        val code = codeMap[p.uniqueId] ?: return false
        if (code == input) {
            codeMap.remove(p.uniqueId)
            return true
        }
        return false
    }
}