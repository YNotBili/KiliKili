package rj.kilikili.ui.components.wear

import androidx.compose.runtime.Stable
import rj.kilikili.ui.components.auto.AppLazyListLayoutInfo
import rj.kilikili.ui.components.auto.AppLazyListItemInfo
import rj.kilikili.ui.components.auto.AppLazyListState
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState

/**
 * [TransformingLazyColumnState] → [AppLazyListState] adapter.
 *
 * Uses `anchorItemIndex` / `anchorItemScrollOffset` to match the
 * [ScalingLazyListState]-based adapter's "center item" semantics.
 */
@Stable
internal class WearTransformingLazyListStateAdapter(
    internal val delegate: TransformingLazyColumnState,
) : AppLazyListState {

    override val firstVisibleItemIndex: Int
        get() = delegate.anchorItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = delegate.anchorItemScrollOffset
    override val isScrollInProgress: Boolean
        get() = delegate.isScrollInProgress
    override val layoutInfo: AppLazyListLayoutInfo
        get() = object : AppLazyListLayoutInfo {
            override val visibleItemsInfo: List<AppLazyListItemInfo>
                get() = delegate.layoutInfo.visibleItems.map { info ->
                    object : AppLazyListItemInfo {
                        override val index: Int get() = info.index
                        override val offset: Int get() = info.offset
                        override val size: Int get() = info.transformedHeight
                    }
                }
        }

    override suspend fun scrollToItem(index: Int, scrollOffset: Int) {
        delegate.scrollToItem(index, scrollOffset)
    }

    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        delegate.animateScrollToItem(index, scrollOffset)
    }
}
