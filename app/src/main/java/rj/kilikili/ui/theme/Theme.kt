package rj.kilikili.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import rj.kilikili.data.proto.NightMode
import rj.kilikili.data.setting.LocalData
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import androidx.wear.compose.material3.ColorScheme as WearColorScheme

@Composable
fun BiliZepamTheme(
    darkTheme: Boolean = when (LocalData.settings.theme.nightMode) {
        NightMode.NIGHT_MODE_DAY -> false
        NightMode.NIGHT_MODE_NIGHT -> true
        else -> isSystemInDarkTheme()
    },
    content: @Composable () -> Unit
) {
    val useSystemAccent = LocalData.settings.theme.followSystemAccent

    val selectedColorThemeKey = LocalData.settings.theme.colorTheme

    val seedColor = AppSeedColors.colorMap[selectedColorThemeKey] ?: AppSeedColors.MATERIAL_PURPLE

    // Android 12+ 使用系统动态颜色（跟随壁纸），否则使用 material-kolor 生成
    val colorScheme = if (useSystemAccent && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        rememberDynamicColorScheme(seedColor = seedColor, isDark = darkTheme, style = PaletteStyle.Expressive)
    }

    val wearColorScheme = colorScheme.toWearColorScheme(isDark = darkTheme)

    // 检测屏幕 shape（基于 LocalConfiguration.current.isScreenRound）
    val configuration = LocalConfiguration.current
    val screenShape = if (configuration.isScreenRound) {
        ScreenShape.ROUND
    } else {
        ScreenShape.SQUARE
    }

    // Phone 端使用官方 Material3 Expressive 主题
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
    ) {
        androidx.wear.compose.material3.MaterialTheme(
            colorScheme = wearColorScheme,
            content = {
                CompositionLocalProvider(
                    LocalContentColor provides colorScheme.onSurface,
                    LocalScreenShape provides screenShape
                ) {
                    content()
                }
            }
        )
    }
}

private fun androidx.compose.material3.ColorScheme.toWearColorScheme(
    isDark: Boolean = false
): WearColorScheme {
    // 暗色模式下覆盖 surface 层次，使用纯黑背景 + 深灰容器（参考 Orbit 方案）
    val bg = if (isDark) WearDarkSurface else background
    val onBg = if (isDark) WearDarkOnSurface else onBackground
    val surfContainerLow = if (isDark) WearDarkSurfaceContainerLow else surfaceVariant
    val surfContainer = if (isDark) WearDarkSurfaceContainer else surfaceVariant
    val surfContainerHigh = if (isDark) WearDarkSurfaceContainerHigh else surface
    val onSurf = if (isDark) WearDarkOnSurface else onSurface

    return WearColorScheme(
        primary = primary,
        primaryDim = primaryContainer,
        primaryContainer = primaryContainer,
        onPrimary = onPrimary,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        secondaryDim = secondaryContainer,
        secondaryContainer = secondaryContainer,
        onSecondary = onSecondary,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        tertiaryDim = tertiaryContainer,
        tertiaryContainer = tertiaryContainer,
        onTertiary = onTertiary,
        onTertiaryContainer = onTertiaryContainer,
        surfaceContainerLow = surfContainerLow,
        surfaceContainer = surfContainer,
        surfaceContainerHigh = surfContainerHigh,
        onSurface = onSurf,
        onSurfaceVariant = onSurfaceVariant,
        background = bg,
        onBackground = onBg,
        error = error,
        errorDim = error,
        errorContainer = errorContainer,
        onError = onError,
        onErrorContainer = onErrorContainer,
        outline = outline,
        outlineVariant = outlineVariant,
    )
}
