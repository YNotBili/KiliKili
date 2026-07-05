package rj.kilikili.ui.screens.recommend

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.auto.appTopBar
import com.huanli233.biliwebapi.bean.video.VideoInfo
import kotlinx.coroutines.launch
import rj.kilikili.ui.components.auto.AppLazyListState
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.ui.components.freshwear.FreshwearSwipeToReveal
import rj.kilikili.ui.components.freshwear.ImmersiveVideoCard
import androidx.wear.compose.material3.rememberRevealState
import rj.kilikili.ui.objects.PhoneCategoryTabRow
import rj.kilikili.ui.objects.SearchActionButton
import rj.kilikili.ui.viewmodel.PopularViewModel
import rj.kilikili.ui.viewmodel.PreciousViewModel
import rj.kilikili.ui.widget.PullToRefreshBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = hiltViewModel(),
    onVideoClick: (VideoInfo) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val scrollState = rememberAppLazyListState()
    var isRefreshing by remember { mutableIntStateOf(0) }
    val isRefreshingBool = isRefreshing > 0
    val configuration = LocalConfiguration.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val categories = remember {
        listOf("推荐", "热门", "必刷", "排行榜", "热门系列", "番剧", "追番时间表")
    }

    LaunchedEffect(videos.loadState.refresh) {
        if (videos.loadState.refresh is LoadState.NotLoading && isRefreshingBool) {
            isRefreshing = 0
        }
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.recommend),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick,
            actions = {
                SearchActionButton(onClick = onSearchClick)
            }
        )
    ) { paddingValues ->
        val currentLoadState = if (isRefreshingBool) LoadState.Loading else videos.loadState.refresh

        Crossfade(
            targetState = currentLoadState,
            animationSpec = tween(durationMillis = 300),
            label = "ContentStateTransition",
            modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                is LoadState.Loading -> {
                    if (videos.itemCount == 0 || isRefreshingBool) {
                        LoadingView(
                            state = LoadingState.LOADING,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ContentView(
                            videos = videos,
                            scrollState = scrollState,
                            paddingValues = paddingValues,
                            isRefreshing = isRefreshingBool,
                            onRefresh = {
                                isRefreshing = 1
                                videos.refresh()
                            },
                            onVideoClick = onVideoClick,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it },
                            categories = categories
                        )
                    }
                }
                is LoadState.Error -> {
                    if (videos.itemCount == 0) {
                        LoadingView(
                            state = LoadingState.ERROR,
                            errorMessage = state.error.message,
                            onRetry = { videos.retry() },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ContentView(
                            videos = videos,
                            scrollState = scrollState,
                            paddingValues = paddingValues,
                            isRefreshing = isRefreshingBool,
                            onRefresh = {
                                isRefreshing = 1
                                videos.refresh()
                            },
                            onVideoClick = onVideoClick,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it },
                            categories = categories
                        )
                    }
                }
                is LoadState.NotLoading -> {
                    if (videos.itemCount == 0) {
                        LoadingView(
                            state = LoadingState.EMPTY,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        ContentView(
                            videos = videos,
                            scrollState = scrollState,
                            paddingValues = paddingValues,
                            isRefreshing = isRefreshingBool,
                            onRefresh = {
                                isRefreshing = 1
                                videos.refresh()
                            },
                            onVideoClick = onVideoClick,
                            selectedTabIndex = selectedTabIndex,
                            onTabSelected = { selectedTabIndex = it },
                            categories = categories
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentView(
    videos: LazyPagingItems<VideoInfo>,
    scrollState: AppLazyListState,
    paddingValues: PaddingValues,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onVideoClick: (VideoInfo) -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    categories: List<String>
) {
    val isPhone = actualUiType == UiType.PHONE

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (isPhone) {
            PhoneRecommendContent(
                videos = videos,
                scrollState = scrollState,
                paddingValues = paddingValues,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected,
                categories = categories,
                onVideoClick = onVideoClick
            )
        } else {
            // Wear / FreshWear — 原有逻辑，无 Tab
            AppLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                contentPadding = paddingValues
            ) {
                items(
                    count = videos.itemCount,
                    key = { index -> videos.peek(index)?.aid?.takeIf { it != 0L } ?: index }
                ) { index ->
                    val video = videos[index]
                    if (video != null && video.bvid.isNotEmpty()) {
                        when (actualUiType) {
                            UiType.FRESHWEAR -> {
                                val revealState = key(video.bvid) { rememberRevealState() }
                                val itemScope = rememberCoroutineScope()
                                FreshwearSwipeToReveal(
                                    revealState = revealState,
                                    autoClose = true,
                                    primaryAction = {
                                        PrimaryActionButton(
                                            onClick = {
                                                itemScope.launch {
                                                    revealState.animateTo(
                                                        androidx.wear.compose.material3.RevealValue.Covered
                                                    )
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Filled.ThumbDown,
                                                    contentDescription = "不感兴趣"
                                                )
                                            },
                                            text = { Text("不感兴趣") }
                                        )
                                    },
                                    onSwipePrimaryAction = { /* no-op for now */ }
                                ) {
                                    ImmersiveVideoCard(
                                        index = index,
                                        listState = scrollState,
                                        videoInfo = video,
                                        onClick = { onVideoClick(video) }
                                    )
                                }
                            }
                            UiType.WEAR, UiType.PHONE -> VideoCard(
                                index = index,
                                listState = scrollState,
                                videoInfo = video,
                                onClick = { onVideoClick(video) }
                            )
                        }
                    }
                }

                item {
                    LoadingFooter(videos)
                }
            }
        }
    }
}

/**
 * Phone 模式推荐页内容 — 顶部 PhoneCategoryTabRow + 根据选中 Tab 切换内容。
 */
@Composable
private fun PhoneRecommendContent(
    videos: LazyPagingItems<VideoInfo>,
    scrollState: AppLazyListState,
    paddingValues: PaddingValues,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    categories: List<String>,
    onVideoClick: (VideoInfo) -> Unit
) {
    AppLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        contentPadding = paddingValues
    ) {
        // 分类 Tab 行（仅 Phone 渲染）
        item {
            PhoneCategoryTabRow(
                categories = categories,
                selectedIndex = selectedTabIndex,
                onSelect = onTabSelected
            )
        }

        when (selectedTabIndex) {
            0 -> {
                // 推荐 — 原有推荐视频列表
                items(
                    count = videos.itemCount,
                    key = { index -> videos.peek(index)?.aid?.takeIf { it != 0L } ?: index }
                ) { index ->
                    val video = videos[index]
                    if (video != null && video.bvid.isNotEmpty()) {
                        VideoCard(
                            index = index,
                            listState = scrollState,
                            videoInfo = video,
                            onClick = { onVideoClick(video) }
                        )
                    }
                }
            }
            1 -> {
                // 热门 — PopularViewModel 驱动的视频列表
                item {
                    PopularTabContent(onVideoClick = onVideoClick)
                }
            }
            2 -> {
                // 必刷 — PreciousViewModel 驱动的视频列表
                item {
                    PreciousTabContent(onVideoClick = onVideoClick)
                }
            }
            else -> {
                // 排行榜 / 热门系列 / 番剧 / 追番时间表 — 占位
                item {
                    PlaceholderTabContent(title = categories[selectedTabIndex])
                }
            }
        }

        item {
            LoadingFooter(videos)
        }
    }
}

/** 热门 Tab 内容 — 内嵌 PopularViewModel 的视频列表（无外层 Scaffold）。 */
@Composable
private fun PopularTabContent(
    viewModel: PopularViewModel = hiltViewModel(),
    onVideoClick: (VideoInfo) -> Unit
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val scrollState = rememberAppLazyListState()
    var isRefreshing by remember { mutableIntStateOf(0) }
    val isRefreshingBool = isRefreshing > 0

    LaunchedEffect(videos.loadState.refresh) {
        if (videos.loadState.refresh is LoadState.NotLoading && isRefreshingBool) {
            isRefreshing = 0
        }
    }

    val currentLoadState = if (isRefreshingBool) LoadState.Loading else videos.loadState.refresh
    Crossfade(targetState = currentLoadState, animationSpec = tween(300), label = "PopularTabState") { state ->
        when (state) {
            is LoadState.Loading -> {
                if (videos.itemCount == 0 || isRefreshingBool) {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxWidth())
                } else {
                    PopularVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
            is LoadState.Error -> {
                if (videos.itemCount == 0) {
                    LoadingView(state = LoadingState.ERROR, errorMessage = state.error.message, onRetry = { videos.retry() }, modifier = Modifier.fillMaxWidth())
                } else {
                    PopularVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
            is LoadState.NotLoading -> {
                if (videos.itemCount == 0) {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxWidth())
                } else {
                    PopularVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
        }
    }
}

@Composable
private fun PopularVideoList(
    videos: LazyPagingItems<VideoInfo>,
    scrollState: AppLazyListState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onVideoClick: (VideoInfo) -> Unit
) {
    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh, modifier = Modifier.fillMaxWidth()) {
        AppLazyColumn(modifier = Modifier.fillMaxWidth(), state = scrollState) {
            items(videos.itemCount) { index ->
                videos[index]?.let { video ->
                    rj.kilikili.ui.components.VideoCard(
                        videoInfo = video,
                        onClick = { onVideoClick(video) }
                    )
                }
            }
        }
    }
}

/** 必刷 Tab 内容 — 内嵌 PreciousViewModel 的视频列表（无外层 Scaffold）。 */
@Composable
private fun PreciousTabContent(
    viewModel: PreciousViewModel = hiltViewModel(),
    onVideoClick: (VideoInfo) -> Unit
) {
    val videos = viewModel.videos.collectAsLazyPagingItems()
    val scrollState = rememberAppLazyListState()
    var isRefreshing by remember { mutableIntStateOf(0) }
    val isRefreshingBool = isRefreshing > 0

    LaunchedEffect(videos.loadState.refresh) {
        if (videos.loadState.refresh is LoadState.NotLoading && isRefreshingBool) {
            isRefreshing = 0
        }
    }

    val currentLoadState = if (isRefreshingBool) LoadState.Loading else videos.loadState.refresh
    Crossfade(targetState = currentLoadState, animationSpec = tween(300), label = "PreciousTabState") { state ->
        when (state) {
            is LoadState.Loading -> {
                if (videos.itemCount == 0 || isRefreshingBool) {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxWidth())
                } else {
                    PreciousVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
            is LoadState.Error -> {
                if (videos.itemCount == 0) {
                    LoadingView(state = LoadingState.ERROR, errorMessage = state.error.message, onRetry = { videos.retry() }, modifier = Modifier.fillMaxWidth())
                } else {
                    PreciousVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
            is LoadState.NotLoading -> {
                if (videos.itemCount == 0) {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxWidth())
                } else {
                    PreciousVideoList(videos, scrollState, isRefreshingBool, { isRefreshing = 1; videos.refresh() }, onVideoClick)
                }
            }
        }
    }
}

@Composable
private fun PreciousVideoList(
    videos: LazyPagingItems<VideoInfo>,
    scrollState: AppLazyListState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onVideoClick: (VideoInfo) -> Unit
) {
    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = onRefresh, modifier = Modifier.fillMaxWidth()) {
        AppLazyColumn(modifier = Modifier.fillMaxWidth(), state = scrollState) {
            items(videos.itemCount) { index ->
                videos[index]?.let { video ->
                    rj.kilikili.ui.components.VideoCard(
                        videoInfo = video,
                        onClick = { onVideoClick(video) }
                    )
                }
            }
        }
    }
}

/** 占位 Tab 内容 — 用于尚未内嵌的分类（排行榜、热门系列、番剧、追番时间表）。 */
@Composable
private fun PlaceholderTabContent(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title 功能开发中…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingFooter(videos: LazyPagingItems<VideoInfo>) {
    when (videos.loadState.append) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            val error = (videos.loadState.append as LoadState.Error).error
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "加载失败: ${error.message}",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        else -> {}
    }
}
