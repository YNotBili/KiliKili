package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import rj.kilikili.UiType
import rj.kilikili.actualUiType

/**
 * 统一 LazyListState 抽象 — wear ScalingLazyListState / phone LazyListState 都适配成此接口。
 * screens 拿到后只调 firstVisibleItemIndex + animateScrollToItem (这两是 wear/phone 共有方法)。
 */
@Stable
interface AppLazyListItemInfo {
    val index: Int
    val offset: Int
    val size: Int
}

interface AppLazyListLayoutInfo {
    val visibleItemsInfo: List<AppLazyListItemInfo>
}

interface AppLazyListState {
    val firstVisibleItemIndex: Int
    val firstVisibleItemScrollOffset: Int
    val isScrollInProgress: Boolean
    val layoutInfo: AppLazyListLayoutInfo
    suspend fun scrollToItem(index: Int, scrollOffset: Int = 0)
    suspend fun animateScrollToItem(index: Int, scrollOffset: Int = 0)
}

fun AppLazyListState.shouldLoadItem(index: Int, prefetch: Int = 3): Boolean {
    val visible = layoutInfo.visibleItemsInfo
    if (visible.isEmpty()) return true
    val minIndex = visible.minOf { it.index }
    val maxIndex = visible.maxOf { it.index }
    return index in minIndex..(maxIndex + prefetch)
}

/**
 * 通用 helper: 从 [AppLazyListState] 取出底层 ScalingLazyListState (wear) 或 null (phone)。
 * 用法: `ScalingLazyColumn(state = listState.asScalingLazyListState() ?: return@AppScreenScaffold) { ... }`
 */
fun AppLazyListState.asScalingLazyListState(): androidx.wear.compose.foundation.lazy.ScalingLazyListState? =
    (this as? rj.kilikili.ui.components.wear.WearLazyListStateAdapter)?.delegate

/**
 * 通用 helper: 从 [AppLazyListState] 取出底层 TransformingLazyColumnState (wear) 或 null (phone)。
 * 用法: `TransformingLazyColumn(state = listState.asTransformingLazyColumnState() ?: return@AppScreenScaffold) { ... }`
 */
fun AppLazyListState.asTransformingLazyColumnState(): androidx.wear.compose.foundation.lazy.TransformingLazyColumnState? =
    (this as? rj.kilikili.ui.components.wear.WearTransformingLazyListStateAdapter)?.delegate

/**
 * 通用 helper: 从 [AppLazyListState] 取出底层 phone LazyListState 或 null (wear)。
 * 用法: `LazyColumn(state = listState.asPhoneLazyListState() ?: return@AppScreenScaffold) { ... }`
 */
fun AppLazyListState.asPhoneLazyListState(): androidx.compose.foundation.lazy.LazyListState? =
    (this as? rj.kilikili.ui.components.phone.PhoneLazyListStateAdapter)?.delegate

/**
 * 统一 remember — 返回 [AppLazyListState] 抽象，wear/phone 内部实现细节 screens 不可见。
 * @param initialFirstVisibleItemIndex wear TransformingLazyColumnState 居中索引，phone 忽略
 * @param initialFirstVisibleItemScrollOffset wear 居中偏移，phone 忽略
 */
@Composable
fun rememberAppLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): AppLazyListState = when (actualUiType) {
    UiType.WEAR, UiType.FRESHWEAR -> rj.kilikili.ui.components.wear.WearTransformingLazyListStateAdapter(
        androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState(
            initialAnchorItemIndex = initialFirstVisibleItemIndex,
            initialAnchorItemScrollOffset = initialFirstVisibleItemScrollOffset
        )
    )
    UiType.PHONE -> rj.kilikili.ui.components.phone.PhoneLazyListStateAdapter(
        androidx.compose.foundation.lazy.rememberLazyListState(
            initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset
        )
    )
}
