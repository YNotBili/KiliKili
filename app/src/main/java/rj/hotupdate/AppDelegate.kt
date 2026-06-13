package rj.hotupdate

import android.app.Application
import android.content.Context

/**
 * 业务 Application 委托接口。
 * 实际 Application 逻辑通过实现此接口放在 assets/main.dex 中，
 * 由 StubApplication 通过 DexClassLoader 加载并调用。
 */
interface AppDelegate {

    /** 初始化，替代 Application.onCreate() */
    fun onCreate(app: Application)

    /** 低内存回调 */
    fun onLowMemory(app: Application)

    /** trimMemory 回调 */
    fun onTrimMemory(app: Application, level: Int)

    /** 获取 Provider 需要的 Context（可选） */
    fun getBaseContext(): Context? = null

    companion object {
        const val DELEGATE_CLASS = "rj.hotupdate.AppDelegateImpl"
    }
}