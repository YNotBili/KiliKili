package rj.kilikili.ui.components.phone

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import rj.kilikili.ui.components.auto.AppLazyListState

/** Phone LazyListState → AppLazyListState 适配。 */
@Stable
internal class PhoneLazyListStateAdapter(
    internal val delegate: LazyListState
) : AppLazyListState {
    override val firstVisibleItemIndex: Int
        get() = delegate.firstVisibleItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = delegate.firstVisibleItemScrollOffset
    override val isScrollInProgress: Boolean
        get() = delegate.isScrollInProgress
    override suspend fun scrollToItem(index: Int, scrollOffset: Int) {
        delegate.scrollToItem(index, scrollOffset)
    }
    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        delegate.animateScrollToItem(index, scrollOffset)
    }
}
