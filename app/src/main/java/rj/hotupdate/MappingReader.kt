package rj.hotupdate

import android.content.Context
import java.io.File

/**
 * 读取 ProGuard/R8 mapping.txt，提供类名和方法名还原。
 *
 * mapping.txt 格式：
 *   com.example.OriginalClass -> com.example.a:
 *       com.example.OriginalMethod(...) -> a
 *       ...
 */
class MappingReader {

    data class ClassMapping(
        val originalName: String,
        val obfuscatedName: String
    )

    data class MethodMapping(
        val originalClass: String,
        val originalName: String,
        val obfuscatedName: String,
        val signature: String = ""
    )

    private val classMappings = mutableMapOf<String, ClassMapping>()
    private val methodMappings = mutableListOf<MethodMapping>()

    fun load(file: File) {
        if (!file.exists()) return
        file.useLines { lines -> parseLines(lines.toList()) }
    }

    fun loadFromAssets(context: Context, path: String = "hotupdate/mapping.txt") {
        try {
            context.assets.open(path).bufferedReader().use { reader ->
                parseLines(reader.readLines())
            }
        } catch (_: Exception) {}
    }

    private fun parseLines(lines: List<String>) {
        var currentClass: ClassMapping? = null
        for (line in lines) {
            if (line.isBlank() || line.startsWith("#")) continue
            if (line.contains(" -> ") && line.endsWith(":")) {
                val arrow = " -> "
                val idx = line.indexOf(arrow)
                if (idx > 0) {
                    val original = line.substring(0, idx)
                    val obfuscated = line.substring(idx + arrow.length, line.length - 1)
                    classMappings[obfuscated] = ClassMapping(original, obfuscated)
                    currentClass = ClassMapping(original, obfuscated)
                }
            } else if (currentClass != null && line.trim().contains(" -> ")) {
                val arrow = " -> "
                val trimmed = line.trim()
                val idx = trimmed.lastIndexOf(arrow)
                if (idx > 0) {
                    val left = trimmed.substring(0, idx).trim()
                    val obfuscated = trimmed.substring(idx + arrow.length).trim()
                    val methodName = left.split(" ").lastOrNull()?.substringBefore("(") ?: left
                    methodMappings.add(MethodMapping(
                        originalClass = currentClass.originalName,
                        originalName = methodName,
                        obfuscatedName = obfuscated,
                        signature = left
                    ))
                }
            }
        }
    }

    fun deobfuscateClass(obfuscatedName: String): String =
        classMappings[obfuscatedName]?.originalName ?: obfuscatedName

    fun deobfuscateStackTrace(stackTrace: String): String {
        var result = stackTrace
        for ((obfuscated, cm) in classMappings) {
            result = result.replace(obfuscated, cm.originalName)
        }
        return result
    }
}