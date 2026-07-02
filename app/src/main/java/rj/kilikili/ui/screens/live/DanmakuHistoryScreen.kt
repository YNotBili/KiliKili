package rj.kilikili.ui.screens.live

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.DanmakuHistoryViewModel

@Composable
fun DanmakuHistoryScreen(
    roomId: Long,
    viewModel: DanmakuHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(roomId) { viewModel.load(roomId) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.danmaku_history),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.items.isEmpty() -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            uiState.error != null && uiState.items.isEmpty() -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(roomId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.items.isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues
                ) {
                    items(
                        count = uiState.items.size,
                        key = { i -> "${uiState.items[i].uid}-${uiState.items[i].text.hashCode()}-$i" }
                    ) { i ->
                        val dm = uiState.items[i]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = dm.uname,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dm.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}