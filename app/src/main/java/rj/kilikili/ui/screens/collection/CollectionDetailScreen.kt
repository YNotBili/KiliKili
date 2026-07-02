package rj.kilikili.ui.screens.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.huanli233.biliwebapi.bean.video.VideoInfo
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appListHeader
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.screens.recommend.VideoCard
import rj.kilikili.ui.viewmodel.CollectionDetailViewModel
import rj.kilikili.ui.widget.PullToRefreshBox

@Composable
fun CollectionDetailScreen(
    mid: Long,
    seasonId: Long,
    title: String,
    viewModel: CollectionDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onVideoClick: (VideoInfo) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(mid, seasonId) {
        viewModel.load(mid, seasonId)
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = uiState.meta?.name?.takeIf { it.isNotEmpty() } ?: title,
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.refresh()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                val showInitialLoading = uiState.isLoading && uiState.videos.isEmpty()
                val showError = uiState.error != null && uiState.videos.isEmpty() && !uiState.isLoading
                val showEmpty = !uiState.isLoading && uiState.videos.isEmpty() && uiState.error == null

                if (showInitialLoading) {
                    LoadingView(
                        state = LoadingState.LOADING,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(screenHeight * 0.7f)
                    )
                } else if (showError) {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier
                            .fillMaxSize()
                            .height(screenHeight * 0.7f)
                    )
                } else if (showEmpty) {
                    LoadingView(
                        state = LoadingState.EMPTY,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(screenHeight * 0.7f)
                    )
                } else {
                    AppLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = paddingValues
                    ) {
                        uiState.meta?.let { meta ->
                            if (meta.description.isNotEmpty() || meta.total > 0) {
                                item {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            if (meta.description.isNotEmpty()) {
                                                Text(
                                                    text = meta.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            Text(
                                                text = "共 ${uiState.total.coerceAtLeast(meta.total)} 集",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            appListHeader(title = "分集列表")
                        }

                        items(
                            count = uiState.videos.size,
                            key = { index -> uiState.videos[index].aid.takeIf { it != 0L } ?: index }
                        ) { index ->
                            val video = uiState.videos[index]
                            if (video.bvid.isNotEmpty()) {
                                VideoCard(
                                    index = index,
                                    listState = scrollState,
                                    videoInfo = video,
                                    onClick = { onVideoClick(video) }
                                )
                            }
                        }

                        if (uiState.hasMore) {
                            item {
                                LaunchedEffect(Unit) { viewModel.loadMore() }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator()
                                    } else {
                                        Text(
                                            text = "上拉加载更多",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        LaunchedEffect(uiState.isLoading, uiState.videos.size) {
            if (!uiState.isLoading && isRefreshing) {
                isRefreshing = false
            }
        }
    }
}