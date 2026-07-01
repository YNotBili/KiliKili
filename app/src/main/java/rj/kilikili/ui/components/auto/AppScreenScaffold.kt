package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import androidx.wear.compose.material3.ScreenScaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreenScaffold(
    modifier: Modifier = Modifier,
    scrollState: AppLazyListState? = null,
    topBar: @Composable () -> Unit = {},
    timeText: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    when (actualUiType) {
        UiType.WEAR -> {
            val wearBehavior = rj.kilikili.ui.components.wear.rememberEnterAlwaysScrollBehavior()
            val wearScrollState = scrollState?.asTransformingLazyColumnState()
                ?: androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState()
            ScreenScaffold(
                modifier = modifier,
                scrollInfoProvider = androidx.wear.compose.foundation.ScrollInfoProvider(wearScrollState),
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
