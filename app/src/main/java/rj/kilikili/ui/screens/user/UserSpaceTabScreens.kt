package rj.kilikili.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.video.VideoInfo
import rj.kilikili.R
import rj.kilikili.ui.components.VideoCard
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.RecentCoinVideosViewModel
import rj.kilikili.ui.viewmodel.RecentLikeVideosViewModel

@Composable
fun RecentCoinVideosScreen(
    mid: Long,
    viewModel: RecentCoinVideosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onVideoClick: (VideoInfo) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    LaunchedEffect(mid) { viewModel.load(mid) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.recent_coin_videos),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.data == null -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            uiState.error != null && uiState.data == null -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(mid) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            (uiState.data?.list ?: emptyList()).isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues
                ) {
                    items(
                        count = uiState.data?.list?.size ?: 0,
                        key = { i -> uiState.data?.list?.getOrNull(i)?.aid ?: i }
                    ) { i ->
                        val v = uiState.data!!.list[i]
                        VideoCard(videoInfo = v, onClick = { onVideoClick(v) })
                    }
                }
            }
        }
    }
}

@Composable
fun RecentLikeVideosScreen(
    mid: Long,
    viewModel: RecentLikeVideosViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    LaunchedEffect(mid) { viewModel.load(mid) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.recent_like_videos),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.data == null -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            uiState.error != null && uiState.data == null -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(mid) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            (uiState.data?.list ?: emptyList()).isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues
                ) {
                    items(
                        count = uiState.data?.list?.size ?: 0,
                        key = { i -> "${uiState.data?.list?.getOrNull(i)?.aid ?: 0}-$i" }
                    ) { i ->
                        val v = uiState.data!!.list[i]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = v.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "时长 ${v.duration}秒",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}