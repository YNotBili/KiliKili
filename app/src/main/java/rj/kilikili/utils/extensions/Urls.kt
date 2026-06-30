package rj.kilikili.utils.extensions

/**
 * 把任意形态的 URL 规范化为 https:
 * - "//host/path" → "https://host/path"
 * - "http://host/path" → "https://host/path"
 * - "https://host/path" → 原样
 * - 空串 / 其它 → 原样
 */
fun String.toHttpsUrl(): String = when {
    isEmpty() -> this
    startsWith("//") -> "https:$this"
    startsWith("http://", ignoreCase = true) -> "https://" + substring(7)
    startsWith("https://", ignoreCase = true) -> this
    else -> this
}
