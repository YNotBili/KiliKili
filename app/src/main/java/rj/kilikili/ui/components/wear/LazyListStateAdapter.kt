package rj.kilikili.ui.components.wear

import androidx.compose.runtime.Stable
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import rj.kilikili.ui.components.auto.AppLazyListState

/**
 * Wear ScalingLazyListState → AppLazyListState 适配 (foundation.lazy 包)。
 * foundation.lazy 用 centerItemIndex (无 current 前缀)。
 */
@Stable
internal class WearLazyListStateAdapter(
    internal val delegate: ScalingLazyListState
) : AppLazyListState {
    override val firstVisibleItemIndex: Int
        get() = delegate.centerItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = delegate.centerItemScrollOffset
    override val isScrollInProgress: Boolean
        get() = delegate.isScrollInProgress
    override suspend fun scrollToItem(index: Int, scrollOffset: Int) {
        delegate.scrollToItem(index, scrollOffset)
    }
    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        delegate.animateScrollToItem(index, scrollOffset)
    }
}
