package rj.hotupdate

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File

/**
 * DEX 加载器。
 * 从 assets 或私有目录加载业务 DEX，通过 DexClassLoader 创建类实例。
 */
class DexLoader(private val context: Context) {

    companion object {
        private const val DEX_ASSET_PATH = "hotupdate/main.dex"
        private const val DEX_FILE_NAME = "main.dex"
        private const val OPTIMIZED_DIR = "dex_opt"
        private const val UPDATED_DIR = "hotupdate"
    }

    data class LoadResult(
        val classLoader: DexClassLoader,
        val isUpdated: Boolean,
        val dexFile: File
    )

    /**
     * 加载 DEX。
     * 1. 检查私有目录是否有更新版 (updated)
     * 2. 否则从 assets 提取到私有目录再加载
     */
    fun load(): LoadResult? {
        val dexDir = context.filesDir
        val updatedDir = File(dexDir, UPDATED_DIR)
        val optimizedDir = File(dexDir, OPTIMIZED_DIR).also { it.mkdirs() }

        // 尝试加载更新版
        val updatedDex = File(updatedDir, DEX_FILE_NAME)
        if (updatedDex.exists()) {
            val loader = DexClassLoader(
                updatedDex.absolutePath,
                optimizedDir.absolutePath,
                null,
                context.classLoader
            )
            return LoadResult(loader, isUpdated = true, dexFile = updatedDex)
        }

        // 从 assets 提取并加载
        return try {
            val dexFile = File(dexDir, DEX_FILE_NAME)
            context.assets.open(DEX_ASSET_PATH).use { input ->
                dexFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val loader = DexClassLoader(
                dexFile.absolutePath,
                optimizedDir.absolutePath,
                null,
                context.classLoader
            )
            LoadResult(loader, isUpdated = false, dexFile = dexFile)
        } catch (_: Exception) {
            null // assets 中可能没有 main.dex
        }
    }

    /** 部署更新文件 */
    fun deployUpdate(source: File): Boolean {
        return try {
            val updatedDir = File(context.filesDir, UPDATED_DIR).also { it.mkdirs() }
            val target = File(updatedDir, DEX_FILE_NAME)
            source.inputStream().use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val flagFile = File(updatedDir, ".updated")
            flagFile.writeText(System.currentTimeMillis().toString())
            true
        } catch (_: Exception) {
            false
        }
    }

    /** 是否有已部署的更新 */
    fun hasUpdate(): Boolean = File(context.filesDir, "$UPDATED_DIR/.updated").exists()
}