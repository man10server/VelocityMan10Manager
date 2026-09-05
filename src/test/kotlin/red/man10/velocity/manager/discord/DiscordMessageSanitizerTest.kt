package red.man10.velocity.manager.discord

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DiscordMessageSanitizerTest {

    private val sanitizer = DiscordMessageSanitizer()

    @ParameterizedTest
    @MethodSource("messages")
    fun `escapes unsupported syntax while preserving permitted formatting`(input: String, expected: String) {
        assertEquals(expected, sanitizer.sanitize(input))
    }

    @ParameterizedTest
    @MethodSource("messages")
    fun `sanitizing twice does not add escapes`(input: String, expected: String) {
        assertEquals(expected, sanitizer.sanitize(sanitizer.sanitize(input)))
    }

    companion object {
        @JvmStatic
        fun messages(): List<Array<String>> = listOf(
            arrayOf("", ""),
            arrayOf("こんにちは！ 12:34 :smile:", "こんにちは！ 12:34 :smile:"),
            arrayOf("**太字** __下線__ *斜体* _斜体_ __***全部***__", "**太字** __下線__ *斜体* _斜体_ __***全部***__"),
            arrayOf("😀 👨‍👩‍👧‍👦 <:wave:123456789012345678> <a:party:234567890123456789>", "😀 👨‍👩‍👧‍👦 <:wave:123456789012345678> <a:party:234567890123456789>"),
            arrayOf("https://example.com/path?q=1#part", """https\://example\.com/path?q=1\#part"""),
            arrayOf("HTTP://localhost:8080 HTTPS://127.0.0.1", """HTTP\://localhost:8080 HTTPS\://127\.0\.0\.1"""),
            arrayOf("ftp://example.com custom+scheme://host", """ftp\://example\.com custom+scheme\://host"""),
            arrayOf("https://example.com/https://other.example", """https\://example\.com/https\://other\.example"""),
            arrayOf("URLはhttps://example.comです", """URLはhttps\://example\.comです"""),
            arrayOf("www.example.com discord.gg/example", """www\.example\.com discord\.gg/example"""),
            arrayOf("<https://example.com>", """\<https\://example\.com\>"""),
            arrayOf("[**名前**](https://example.com)", """\[**名前**\](https\://example\.com)"""),
            arrayOf("`https://example.com`", """\`https\://example\.com\`"""),
            arrayOf("```\nhttps://example.com\n```", "\\`\\`\\`\nhttps\\://example\\.com\n\\`\\`\\`"),
            arrayOf("~~削除~~ ||秘密||", "~~削除~~ ||秘密||"),
            arrayOf("~~https://example.com~~", """~~https\://example\.com~~"""),
            arrayOf("""\~\~そのまま\~\~""", """\~\~そのまま\~\~"""),
            arrayOf("||**秘密** 😀||", "||**秘密** 😀||"),
            arrayOf("||https://example.com||", """||https\://example\.com||"""),
            arrayOf("""\|\|そのまま\|\|""", """\|\|そのまま\|\|"""),
            arrayOf("# 見出し\n-# 小文字\n> 引用", "\\# 見出し\n-\\# 小文字\n\\> 引用"),
            arrayOf("* 項目\n  - 項目\n+ 項目\n1. 項目", "\\* 項目\n  \\- 項目\n\\+ 項目\n1\\. 項目"),
            arrayOf("<t:1234567890:R> <@123456789012345678>", """\<t:1234567890:R\> \<@123456789012345678\>"""),
            arrayOf("""https\://example\.com \*そのまま\*""", """https\://example\.com \*そのまま\*"""),
            arrayOf("""\\`https://example.com`""", """\\\`https\://example\.com\`"""),
            arrayOf("末尾\\", "末尾\\\\")
        )
    }
}
