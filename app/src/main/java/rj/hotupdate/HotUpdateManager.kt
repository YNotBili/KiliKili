package rj.hotupdate

import android.app.Application
import android.util.Log
import java.io.File

/**
 * 热更新管理器 - 单例。
 *
 * 职责：
 * - 加载业务 DEX
 * - 实例化 AppDelegate
 * - 委托 Application 生命周期
 * - 检查更新标志
 */
class HotUpdateManager private constructor() {

    companion object {
        private const val TAG = "HotUpdate"

        @Volatile
        private var instance: HotUpdateManager? = null

        fun get(): HotUpdateManager =
            instance ?: synchronized(this) {
                instance ?: HotUpdateManager().also { instance = it }
            }
    }

    private var delegate: AppDelegate? = null
    private var dexLoader: DexLoader? = null
    private var mappingReader: MappingReader? = null

    val mapping: MappingReader? get() = mappingReader

    /** 初始化热更新系统 */
    fun initialize(app: Application) {
        val startTime = System.currentTimeMillis()

        // 1. 加载 mapping
        mappingReader = MappingReader().apply {
            loadFromAssets(app)
            val homeDir = File(app.filesDir, "hotupdate")
            val mappingFile = File(homeDir, "mapping.txt")
            if (mappingFile.exists()) load(mappingFile)
        }

        // 2. 加载 DEX
        dexLoader = DexLoader(app)
        val result = dexLoader?.load()

        if (result != null) {
            try {
                val clazz = result.classLoader.loadClass(AppDelegate.DELEGATE_CLASS)
                delegate = clazz.getDeclaredConstructor().newInstance() as AppDelegate
                delegate?.onCreate(app)
                Log.i(TAG, "AppDelegate initialized via DEX (updated=${result.isUpdated}) in ${System.currentTimeMillis() - startTime}ms")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load AppDelegate from DEX", e)
            }
        } else {
            // 无 DEX，可能是首次构建且未启用 dexOverride
            Log.w(TAG, "No main.dex found, falling back to normal init")
        }
    }

    /** 是否有可用更新 */
    fun hasUpdate(): Boolean = dexLoader?.hasUpdate() ?: false

    /** 部署更新 */
    fun deployUpdate(dexFile: File): Boolean = dexLoader?.deployUpdate(dexFile) ?: false

    /** 委派低内存回调 */
    fun onLowMemory(app: Application) {
        delegate?.onLowMemory(app)
    }

    /** 委派 trimMemory */
    fun onTrimMemory(app: Application, level: Int) {
        delegate?.onTrimMemory(app, level)
    }
}