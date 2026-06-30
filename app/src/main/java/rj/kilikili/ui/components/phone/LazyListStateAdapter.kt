package rj.kilikili.ui.components.phone

import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Stable
import rj.kilikili.ui.components.auto.AppLazyListItemInfo
import rj.kilikili.ui.components.auto.AppLazyListLayoutInfo
import rj.kilikili.ui.components.auto.AppLazyListState

/** Phone LazyListState → AppLazyListState 适配。 */
@Stable
private class PhoneItemInfo(
    override val index: Int,
    override val offset: Int,
    override val size: Int
) : AppLazyListItemInfo

private class PhoneLayoutInfo(
    private val delegate: LazyListLayoutInfo
) : AppLazyListLayoutInfo {
    override val visibleItemsInfo: List<AppLazyListItemInfo>
        get() = delegate.visibleItemsInfo.map {
            PhoneItemInfo(it.index, it.offset, it.size)
        }
}

internal class PhoneLazyListStateAdapter(
    internal val delegate: LazyListState
) : AppLazyListState {
    override val firstVisibleItemIndex: Int
        get() = delegate.firstVisibleItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = delegate.firstVisibleItemScrollOffset
    override val isScrollInProgress: Boolean
        get() = delegate.isScrollInProgress
    override val layoutInfo: AppLazyListLayoutInfo
        get() = PhoneLayoutInfo(delegate.layoutInfo)
    override suspend fun scrollToItem(index: Int, scrollOffset: Int) {
        delegate.scrollToItem(index, scrollOffset)
    }
    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        delegate.animateScrollToItem(index, scrollOffset)
    }
}
