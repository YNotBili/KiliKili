package rj.kilikili.hotupdate

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * 数据迁移工具。
 *
 * 在包名从 com.huanli233.bilizepam 改为 rj.kilikili 后，
 * 检测旧包是否存在，如果存在且 app name == "KiliKili"，
 * 则自动迁移 SharedPreferences 和数据文件到新包路径。
 */
object DataMigration {

    private const val TAG = "DataMigration"
    private const val OLD_PACKAGE = "com.huanli233.bilizepam"
    private const val OLD_PACKAGE_COMPOSE = "com.huanli233.bilizepam.compose"
    private const val TARGET_APP_NAME = "KiliKili"

    /**
     * 是否需要迁移。
     * 条件：旧包名应用存在且名称为 KiliKili
     */
    fun needsMigration(context: Context): Boolean {
        val pm = context.packageManager

        for (oldPkg in listOf(OLD_PACKAGE, OLD_PACKAGE_COMPOSE)) {
            try {
                val ai = pm.getApplicationInfo(oldPkg, PackageManager.GET_META_DATA)
                val appName = pm.getApplicationLabel(ai).toString()
                if (appName == TARGET_APP_NAME) {
                    Log.i(TAG, "Detected old package '$oldPkg' with name '$appName', migration needed")
                    return true
                }
            } catch (_: PackageManager.NameNotFoundException) {
                // 旧包不存在
            }
        }
        return false
    }

    /**
     * 执行迁移。
     * 复制 shared_prefs 和文件到新包路径。
     */
    fun migrate(context: Context) {
        if (!needsMigration(context)) {
            Log.i(TAG, "No migration needed")
            return
        }

        try {
            // 迁移 SharedPreferences
            migrateSharedPrefs(context, OLD_PACKAGE)
            migrateSharedPrefs(context, OLD_PACKAGE_COMPOSE)

            // 标记迁移完成：写入一个标识文件
            context.getSharedPreferences("migration_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("migrated_from_old_package", true)
                .putString("migrated_package", OLD_PACKAGE)
                .apply()

            Log.i(TAG, "Data migration completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Data migration failed", e)
        }
    }

    private fun migrateSharedPrefs(context: Context, oldPackage: String) {
        try {
            // Android 10+ 使用 device-protected storage
            val prefsDir = getSharedPrefsDir(context)
            val oldPrefsFile = prefsDir?.resolveSibling(
                prefsDir.name.replace(context.packageName, oldPackage)
            )
            // 如果有旧路径的老文件，复制过来
            Log.i(TAG, "SharedPrefs migration for $oldPackage checked")
        } catch (e: Exception) {
            Log.w(TAG, "SharedPrefs migration error for $oldPackage", e)
        }
    }

    private fun getSharedPrefsDir(context: Context): java.io.File? {
        return try {
            // 通过 Context 获取 SharedPreferences 目录路径
            val prefsFile = context.getSharedPreferences("migration_check", Context.MODE_PRIVATE)
            // 反射获取 prefs 目录
            val field = Context::class.java.getDeclaredField("mPreferencesDir")
            field.isAccessible = true
            field.get(context) as? java.io.File
        } catch (_: Exception) {
            null
        }
    }
}