package rj.kilikili

/** 当前 UI 目标平台。WEAR=强制手表, PHONE=强制手机。 */
enum class UiType { WEAR, PHONE }

/**
 * 全局 UI 类型 — 启动时从 LocalData 读取, 用户在设置中切换时实时更新。
 * auto 包 [actualUiType] 读这个值。
 */
@Volatile
var uiType: UiType = UiType.WEAR

/** Int (proto 字段) → UiType 转换。0 或未设置 = WEAR (默认手表), 1 = PHONE。 */
internal fun Int.toUiTypeOrWear(): UiType = when (this) {
    1 -> UiType.PHONE
    else -> UiType.WEAR
}
