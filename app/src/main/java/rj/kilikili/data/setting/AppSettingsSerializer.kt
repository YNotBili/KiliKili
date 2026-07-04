package rj.kilikili.data.setting

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.InvalidProtocolBufferException
import rj.kilikili.data.menu.MenuConfig
import rj.kilikili.data.proto.ApiCache
import rj.kilikili.data.proto.AppSettings
import rj.kilikili.data.proto.DanmakuSource
import rj.kilikili.data.proto.ImageFormat
import rj.kilikili.data.proto.NightMode
import rj.kilikili.data.proto.PlayerSettings
import rj.kilikili.data.proto.Preferences
import rj.kilikili.data.proto.Theme
import rj.kilikili.data.proto.UiSettings
import java.io.InputStream
import java.io.OutputStream


inline fun <M : GeneratedMessageLite<M, B>, B : GeneratedMessageLite.Builder<M, B>> GeneratedMessageLite<M, B>.edit(
    block: B.() -> Unit
): M = toBuilder().apply(block).build()

inline fun <M : GeneratedMessageLite<M, B>, B : GeneratedMessageLite.Builder<M, B>> B.build(
    block: B.() -> Unit
): M = apply(block).build()

object AppSettingsSerializer : Serializer<AppSettings> {
    override val defaultValue: AppSettings = AppSettings.newBuilder().build {
        activeAccountId = 0L
        firstRun = true

        uiSettings = UiSettings.newBuilder().build {
            roundMode = false
            uiScale = 1.0f
            uiPaddingHorizontal = 0
            uiPaddingVertical = 0
            density = 0
            snackbarEnabled = true
            marqueeEnabled = true
            gridListEnabled = false
            videoCardBackgroundStyle = false
            userProfileBackgroundEnabled = false
            collectionCardBackgroundStyle = false
            favoriteFolderCardBackgroundStyle = false
        }
        theme = Theme.newBuilder().build {
            nightMode = NightMode.NIGHT_MODE_NIGHT
            followSystemAccent = false
            colorTheme = "DEFAULT"
            animationsEnabled = true
            fullScreenDialogDisabled = false
        }
        apiCache = ApiCache.newBuilder().build {
            wbiMixinKey = ""
            wbiLastUpdated = 0L
        }
        preferences = Preferences.newBuilder().build {
            backDisabled = false
            stopLoadImageWhileScrolling = false
            imageFormat = ImageFormat.IMAGE_FORMAT_JPEG
            asyncInflateEnabled = false
        }
        playerSettings = PlayerSettings.newBuilder().build {
            useSoftwareDecoder = false
            autoPlay = true
            defaultQuality = 64
            useTextureView = true
            enableOneFingerZoom = true
            defaultDanmakuEnabled = true
            defaultSpeed = 1.0f
            rememberDanmakuEnabled = false
            rememberSpeed = false
            danmakuFontSize = 16f
            danmakuMaxCount = 50
            danmakuScrollEnabled = true
            danmakuTopEnabled = true
            danmakuBottomEnabled = true
            danmakuAdvancedEnabled = true
            danmakuTransparency = 0.8f
            danmakuScrollSpeed = 1.0f
            danmakuStrokeWidth = 3f
            danmakuMergeDuplicate = true
            danmakuBold = false
            danmakuAreaTop = 0.0f
            danmakuAreaBottom = 0.0f
            danmakuSource = DanmakuSource.DANMAKU_SOURCE_PROTOBUF
        }
        menuConfig = MenuConfig().toString()
    }

    override suspend fun readFrom(input: InputStream): AppSettings {
        try {
            val settings = AppSettings.parseFrom(input)
            // 迁移：旧版默认 SurfaceView(false)，新版默认 TextureView(true)
            // 迁移：旧版单指缩放默认关闭(false)，新版默认开启(true)
            // 对于从未主动设置过这两项的老用户，proto3 存储的值为 false，
            // 此处将其迁移为新默认值 true
            return if (!settings.playerSettings.useTextureView || !settings.playerSettings.enableOneFingerZoom) {
                settings.edit {
                    playerSettings = playerSettings.edit {
                        if (!useTextureView) useTextureView = true
                        if (!enableOneFingerZoom) enableOneFingerZoom = true
                    }
                }
            } else {
                settings
            }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        t.writeTo(output)
    }
}