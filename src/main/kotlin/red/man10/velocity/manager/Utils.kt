package red.man10.velocity.manager

import com.google.gson.JsonParser
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import java.io.File
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.Base64
import java.util.jar.JarFile

object Utils {

    private val textureUrlRegex = Regex("""/texture/([0-9a-f]+)$""")

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

    fun Player.getTextureHash(): String? = runCatching {
        val encoded = gameProfileProperties
            .firstOrNull { it.name == "textures" }
            ?.value ?: return null
        val json = JsonParser.parseString(String(Base64.getDecoder().decode(encoded))).asJsonObject
        val url = json.getAsJsonObject("textures")
            .getAsJsonObject("SKIN")
            .get("url").asString
        textureUrlRegex.find(url)?.groupValues?.get(1)
    }.getOrNull()

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