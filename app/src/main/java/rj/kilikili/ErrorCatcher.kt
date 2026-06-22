package rj.kilikili

import android.content.Context
import rj.kilikili.ui.activity.CrashActivity
import java.lang.ref.WeakReference

class ErrorCatcher : Thread.UncaughtExceptionHandler {
    private var context: Context? = null

    fun install(context: Context) {
        this.context = context
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            context?.startActivity(CrashActivity.createIntent(context!!, throwable))
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        throwable.printStackTrace()
    }

    companion object {
        private var _instance: WeakReference<ErrorCatcher> = WeakReference(null)
        val instance: ErrorCatcher
            get() = _instance.get() ?: ErrorCatcher().also {
                _instance = WeakReference(it)
            }
    }
}
