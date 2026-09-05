package red.man10.velocity.manager.discord

class DiscordMessageSanitizer {

    private val tokens = Regex(
        """<a?:[A-Za-z0-9_]+:[0-9]+>|\\[\\`~|<>\[\]#*_:.+()\-]|(?i:[a-z][a-z0-9+.-]*):(?=//)|[\\`<>\[\]#.]"""
    )
    private val listMarker = Regex("""(?m)^([\t ]*)([*+-])(?=[\t ])""")

    /**
     * 太字・下線・斜体・取り消し線・スポイラーと絵文字を許可する、Discord 向けの本文エスケープ。
     * URL のスキームのコロンとドットをエスケープし、既存のエスケープは維持する。
     * メンション通知の制御は送信時の allowed_mentions で行う。
     */
    fun sanitize(message: String): String {
        val escaped = tokens.replace(message) {
            val token = it.value
            when {
                token.startsWith('<') && token.length > 1 -> token
                token.startsWith('\\') && token.length == 2 -> token
                token.endsWith(':') -> token.dropLast(1).replace(".", "\\.") + "\\:"
                else -> "\\$token"
            }
        }
        return listMarker.replace(escaped) { "${it.groupValues[1]}\\${it.groupValues[2]}" }
    }
}
