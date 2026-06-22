package rj.kilikili.ui.screens.ranking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.ScrollAwareTopBar
import rj.kilikili.ui.components.VideoCard
import rj.kilikili.ui.components.rememberEnterAlwaysScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.RankingUiState
import rj.kilikili.ui.viewmodel.RankingViewModel
import com.huanli233.biliwebapi.bean.video.VideoInfo
import kotlinx.coroutines.launch

@Composable
fun RankingScreen(
    onVideoClick: (VideoInfo) -> Unit,
    onNavigateBack: () -> Unit,
    onMenuClick: () -> Unit = {},
    viewModel: RankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScalingLazyListState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberPullToRefreshState()
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()

    ScreenScaffold(
        scrollState = scrollState,
        topBar = {
            ScrollAwareTopBar(
                title = stringResource(R.string.ranking),
                scrollBehavior = scrollBehavior,
                showBackIcon = false,
                showMenuIcon = true,
                onMenuClick = onMenuClick
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        PullToRefreshBox(
            state = swipeRefreshState,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.loadRanking()
                    isRefreshing = false
                }
            },
            isRefreshing = isRefreshing,
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = uiState) {
                is RankingUiState.Loading -> {
                    LoadingView(
                        state = LoadingState.LOADING,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is RankingUiState.Error -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = state.message,
                        onRetry = { viewModel.loadRanking() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is RankingUiState.Success -> {
                    if (state.items.isEmpty()) {
                        LoadingView(
                            state = LoadingState.EMPTY,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        RankingList(
                            items = state.items,
                            scrollState = scrollState,
                            paddingValues = paddingValues,
                            onVideoClick = onVideoClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingList(
    items: List<VideoInfo>,
    scrollState: androidx.wear.compose.foundation.lazy.ScalingLazyListState,
    paddingValues: PaddingValues,
    onVideoClick: (VideoInfo) -> Unit
) {
    ScalingLazyColumn(
        state = scrollState,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.aid }) { videoInfo ->
            VideoCard(
                videoInfo = videoInfo,
                onClick = { onVideoClick(videoInfo) }
            )
        }
    }
}