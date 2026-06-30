package rj.kilikili.ui.components.wear

import androidx.compose.runtime.Stable
import androidx.wear.compose.foundation.lazy.ScalingLazyListLayoutInfo
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import rj.kilikili.ui.components.auto.AppLazyListItemInfo
import rj.kilikili.ui.components.auto.AppLazyListLayoutInfo
import rj.kilikili.ui.components.auto.AppLazyListState

/**
 * Wear ScalingLazyListState → AppLazyListState 适配 (foundation.lazy 包)。
 * foundation.lazy 用 centerItemIndex (无 current 前缀)。
 */
@Stable
private class WearItemInfo(
    override val index: Int,
    override val offset: Int,
    override val size: Int
) : AppLazyListItemInfo

private class WearLayoutInfo(
    private val delegate: ScalingLazyListLayoutInfo
) : AppLazyListLayoutInfo {
    override val visibleItemsInfo: List<AppLazyListItemInfo>
        get() = delegate.visibleItemsInfo.map {
            WearItemInfo(it.index, it.offset, it.size)
        }
}

internal class WearLazyListStateAdapter(
    internal val delegate: ScalingLazyListState
) : AppLazyListState {
    override val firstVisibleItemIndex: Int
        get() = delegate.centerItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = delegate.centerItemScrollOffset
    override val isScrollInProgress: Boolean
        get() = delegate.isScrollInProgress
    override val layoutInfo: AppLazyListLayoutInfo
        get() = WearLayoutInfo(delegate.layoutInfo)
    override suspend fun scrollToItem(index: Int, scrollOffset: Int) {
        delegate.scrollToItem(index, scrollOffset)
    }
    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        delegate.animateScrollToItem(index, scrollOffset)
    }
}
