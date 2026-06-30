package rj.kilikili.ui.components.phone

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import rj.kilikili.ui.components.auto.AppScrollBehavior
import rj.kilikili.ui.components.auto.AppTopBarState

/** phone 端 AppTopBarState adapter — 包装 Material3 TopAppBarState。 */
@OptIn(ExperimentalMaterial3Api::class)
internal class PhoneTopBarStateAdapter(
    internal val delegate: TopAppBarState
) : AppTopBarState {
    override val collapsedFraction: Float
        get() = delegate.collapsedFraction
    override val heightOffset: Float
        get() = delegate.heightOffset
}

/** phone 端 AppScrollBehavior adapter — 包装 Material3 TopAppBarScrollBehavior。 */
@OptIn(ExperimentalMaterial3Api::class)
internal class PhoneScrollBehaviorAdapter(
    internal val delegate: TopAppBarScrollBehavior
) : AppScrollBehavior {
    override val nestedScrollConnection
        get() = delegate.nestedScrollConnection
    override val state: AppTopBarState
        get() = PhoneTopBarStateAdapter(delegate.state)
    override val isPinned: Boolean
        get() = delegate.isPinned
    override val snapAnimationSpec
        get() = delegate.snapAnimationSpec
    override val flingAnimationSpec
        get() = delegate.flingAnimationSpec
}
