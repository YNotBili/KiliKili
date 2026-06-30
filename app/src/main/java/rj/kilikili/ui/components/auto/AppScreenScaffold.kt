package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold as phoneScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.ui.components.phone.PhoneScrollBehaviorAdapter
import rj.kilikili.ui.components.wear.TopBarScrollBehavior
import rj.kilikili.ui.components.wear.WearLazyListStateAdapter
import androidx.wear.compose.material3.ScreenScaffold as wearScreenScaffold
/**
 * 统一 Scaffold 入口 — wear 端走 ScreenScaffold (content 是 BoxScope 扩展),
 * phone 端走 Material3 Scaffold。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreenScaffold(
    modifier: Modifier = Modifier,
    scrollState: Any? = null,
    topBar: @Composable () -> Unit = {},
    topBarScrollBehavior: AppScrollBehavior? = null,
    timeText: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    if (actualUiType == UiType.WEAR) {
        val wearBehavior = topBarScrollBehavior as? TopBarScrollBehavior
            ?: rj.kilikili.ui.components.wear.rememberEnterAlwaysScrollBehavior()
        val wearScrollState = (scrollState as? WearLazyListStateAdapter)?.delegate
            ?: (scrollState as? androidx.wear.compose.foundation.lazy.ScalingLazyListState)
            ?: androidx.wear.compose.foundation.lazy.rememberScalingLazyListState()
        Box(modifier = modifier) {
            wearScreenScaffold(
                scrollState = wearScrollState,
                topBar = topBar,
                topBarScrollBehavior = wearBehavior,
                timeText = timeText
            ) { padding ->
                content(padding)
            }
        }
    } else {
        phoneScaffold(
            modifier = modifier,
            topBar = topBar,
            content = content
        )
    }
}
