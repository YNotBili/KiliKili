package rj.kilikili.ui.components.auto

import rj.kilikili.UiType
import rj.kilikili.uiType

/**
 * 实际 UI 类型 — 用于 screens/auto 包内 when 分发。
 * 永远返回 WEAR 或 PHONE，运行时由用户设置覆盖。
 * 读 [uiType] 是 volatile var, 修改后立即对所有 Composable 可见 (重组触发)。
 */
@PublishedApi
internal val actualUiType: UiType
    get() = uiType
