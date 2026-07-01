package rj.kilikili

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.printer.AndroidPrinter
import rj.kilikili.api.setOkHttpSsl
import rj.kilikili.data.setting.LocalData
import rj.kilikili.data.setting.toSystemValue
import rj.kilikili.utils.locale.LocaleDelegate
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.util.Locale

@HiltAndroidApp
class KiliKili : MultiDexApplication(), SingletonImageLoader.Factory {
    init {
        application = this
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        contextNullable = base
    }

    private var lastThemeMode: Int = -1

    override fun onCreate() {
        super.onCreate()
        XLog.init(
            if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.ERROR,
            AndroidPrinter()
        )
        contextNullable = applicationContext
        LocaleDelegate.defaultLocale = getLocale()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            @Suppress("DEPRECATION") resources.updateConfiguration(resources.configuration.apply {
                setLocale(LocaleDelegate.defaultLocale)
            }, resources.displayMetrics)
        }
        lastThemeMode = LocalData.settings.theme.nightMode.toSystemValue()
        setDefaultNightMode(lastThemeMode)
        LocaleDelegate.defaultLocale = getLocale()
        // 同步启动时持久化的 UI 类型
        uiType = LocalData.settings.uiType.toUiTypeOrWear()
        applicationScope.launch {
            LocalData.settingsStateFlow.collectLatest { config ->
                config?.let {
                    if (lastThemeMode != it.theme.nightMode.toSystemValue()) {
                        lastThemeMode = it.theme.nightMode.toSystemValue()
                        withContext(Dispatchers.Main) {
                            setDefaultNightMode(lastThemeMode)
                        }
                    }
                    // 同步设置里的 uiType 到全局
                    val newUiType = it.uiType.toUiTypeOrWear()
                    if (uiType != newUiType) {
                        uiType = newUiType
                    }
                    // Update locale when language changes
                    val newLocale = getLocale(it.language)
                    if (LocaleDelegate.defaultLocale != newLocale) {
                        LocaleDelegate.defaultLocale = newLocale
                    }
                }
            }
        }
        ErrorCatcher.instance.install(applicationContext)
    }

    fun getLocale(tag: String): Locale {
        if (tag.isEmpty() || "SYSTEM" == tag || Build.VERSION.SDK_INT < 21) {
            return LocaleDelegate.systemLocale
        }
        return Locale.forLanguageTag(tag)
    }

    fun getLocale(): Locale {
        val tag = LocalData.settings.language
        return getLocale(tag)
    }

    // Coil 图片加载 — 复用忽略证书的 OkHttp, 保证 HTTPS 图片在自签/代理场景也能加载。
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val okHttpClient = setOkHttpSsl(OkHttpClient.Builder()).build()
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
            }
            .crossfade(true)
            .build()
    }

    companion object {
        lateinit var application: KiliKili
            private set

        fun setDefaultNightMode(mode: Int) {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}

val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

@JvmField
@SuppressLint("StaticFieldLeak")
var contextNullable: Context? = null

val applicationContext: Context
    get() = contextNullable!!
