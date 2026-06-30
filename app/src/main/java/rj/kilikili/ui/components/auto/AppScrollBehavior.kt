package rj.kilikili.ui.components.auto

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Stable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

/**
 * 统一 TopBar 状态 — wear/phone 各自的 TopBarState 适配成此接口。
 * screens 拿到 [AppScrollBehavior.state] 后只调这两个属性。
 */
@Stable
interface AppTopBarState {
    /** 0.0 = 完全展开, 1.0 = 完全折叠 */
    val collapsedFraction: Float
    /** 负值 = 折叠量 (px) */
    val heightOffset: Float
}

/**
 * 统一 scroll behavior 抽象 — wear/phone 各自实现。
 * screens 只调 [nestedScrollConnection]。
 */
@Stable
interface AppScrollBehavior {
    val nestedScrollConnection: NestedScrollConnection
    val state: AppTopBarState
    val isPinned: Boolean
    val snapAnimationSpec: AnimationSpec<Float>?
    val flingAnimationSpec: DecayAnimationSpec<Float>?
}
