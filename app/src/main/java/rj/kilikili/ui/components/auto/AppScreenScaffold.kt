package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.uiType
import rj.kilikili.ui.components.wear.WearLazyListStateAdapter
import androidx.wear.compose.material3.ScreenScaffold

/**
 * 统一 Scaffold 入口 — wear 端走 ScreenScaffold, phone 端走 Material3 Scaffold。
 * topBar: phone 端直接传给 Scaffold(topBar=), wear 端放在 ScreenScaffold topBar 槽位。

 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreenScaffold(
    modifier: Modifier = Modifier,
    scrollState: Any? = null,
    topBar: @Composable () -> Unit = {},
    timeText: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    when (actualUiType) {
        UiType.WEAR -> {
            val wearBehavior = rj.kilikili.ui.components.wear.rememberEnterAlwaysScrollBehavior()
            val wearScrollState = (scrollState as? WearLazyListStateAdapter)?.delegate
                ?: (scrollState as? androidx.wear.compose.foundation.lazy.ScalingLazyListState)
                ?: androidx.wear.compose.foundation.lazy.rememberScalingLazyListState()
            ScreenScaffold(
                modifier = modifier,
                scrollState = wearScrollState,
                topBar = topBar,
                topBarScrollBehavior = wearBehavior,
                timeText = timeText
            ) { padding ->
                content(padding)
            }
        }
        UiType.PHONE -> {
            Scaffold(
                modifier = modifier,
                topBar = topBar,
                content = content
            )
        }
    }
}
