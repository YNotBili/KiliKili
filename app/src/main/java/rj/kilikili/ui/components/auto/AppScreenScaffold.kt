package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    when (actualUiType) {
        UiType.WEAR, UiType.FRESHWEAR -> {
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
            val phoneScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                rememberTopAppBarState()
            )
            CompositionLocalProvider(
                LocalPhoneTopBarScrollBehavior provides phoneScrollBehavior
            ) {
                Scaffold(
                    modifier = modifier.nestedScroll(phoneScrollBehavior.nestedScrollConnection),
                    topBar = topBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    content = content
                )
            }
        }
    }
}
