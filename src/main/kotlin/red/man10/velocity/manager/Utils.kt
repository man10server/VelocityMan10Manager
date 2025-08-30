package red.man10.velocity.manager

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import java.io.File
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.jar.JarFile

object Utils {

    fun getClasses(url: URL, packageName: String): List<Class<*>> {
        val classes = ArrayList<Class<*>>()
        val src = ArrayList<File>()
        val srcFile = try {
            File(url.toURI())
        } catch (_: IllegalArgumentException) {
            File((url.openConnection() as JarURLConnection).jarFileURL.toURI())
        } catch (_: URISyntaxException) {
            File(url.path)
        }

        src += srcFile

        src.forEach { s ->
            JarFile(s).stream().filter { it.name.endsWith(".class") }.forEach second@ {
                val name = it.name.replace('/', '.').substring(0, it.name.length - 6)
                if (!name.contains(packageName)) return@second

                kotlin.runCatching {
                    classes.add(Class.forName(name, false, VelocityMan10Manager::class.java.classLoader))
                }
            }
        }

        return classes
    }

    fun Player.getServerName(): String {
        return this.currentServer.map { it.serverInfo.name }.orElse("N/A")
    }

    fun CommandSource.getName(): String {
        return if (this is Player) {
            this.username
        } else {
            "Console"
        }
    }

    // 文字列テンプレートに %key% 形式のプレースホルダーを適用する拡張関数（Utils メンバー）
    fun String.applyPlaceholders(placeholders: Map<String, String>): String =
        placeholders.entries.fold(this) { acc, entry ->
            acc.replace("%${entry.key}%", entry.value)
        }
}