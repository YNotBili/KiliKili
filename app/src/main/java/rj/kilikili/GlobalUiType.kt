package rj.kilikili

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/** 当前 UI 目标平台。WEAR=强制手表, PHONE=强制手机, FRESHWEAR=Orbit 风格手表。 */
enum class UiType { WEAR, PHONE, FRESHWEAR }

/**
 * 全局 UI 类型 — 启动时从 LocalData 读取, 用户在设置中切换时实时更新。
 *
 * 用 [MutableState] 包装, Compose 会自动重组读取 [actualUiType] 的 Composable。
 */
private val uiTypeState: MutableState<UiType> = mutableStateOf(UiType.WEAR)

/** 直接读写 (setter 触发 Compose 重组)。 */
var uiType: UiType
    get() = uiTypeState.value
    set(value) { uiTypeState.value = value }

/** 实际 UI 类型 — 用于 when 分发, 在 Composable 中读取会自动重组。 */
val actualUiType: UiType
    @androidx.compose.runtime.ReadOnlyComposable
    get() = uiTypeState.value

/** Int (proto 字段) → UiType 转换。0 或未设置 = WEAR (默认手表), 1 = PHONE, 2 = FRESHWEAR。 */
internal fun Int.toUiTypeOrWear(): UiType = when (this) {
    1 -> UiType.PHONE
    2 -> UiType.FRESHWEAR
    else -> UiType.WEAR
}