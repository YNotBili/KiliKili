package rj.kilikili.ui.screens.topic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.TopicViewModel
import rj.kilikili.utils.MsgUtil

@Composable
fun TopicScreen(
    topicId: Long,
    viewModel: TopicViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onItemClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(topicId, uiState.tab) {
        if (uiState.tab == TopicViewModel.Tab.Feed) viewModel.loadFeed(topicId)
        else viewModel.loadRcmd(topicId)
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.topic),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = if (uiState.tab == TopicViewModel.Tab.Feed) 0 else 1) {
                Tab(
                    selected = uiState.tab == TopicViewModel.Tab.Feed,
                    onClick = { viewModel.setTab(TopicViewModel.Tab.Feed) },
                    text = { Text(stringResource(R.string.topic_feed)) }
                )
                Tab(
                    selected = uiState.tab == TopicViewModel.Tab.Rcmd,
                    onClick = { viewModel.setTab(TopicViewModel.Tab.Rcmd) },
                    text = { Text(stringResource(R.string.topic_recommend)) }
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                    }
                    uiState.error != null -> {
                        LoadingView(
                            state = LoadingState.ERROR,
                            errorMessage = uiState.error,
                            onRetry = {
                                if (uiState.tab == TopicViewModel.Tab.Feed) viewModel.loadFeed(topicId)
                                else viewModel.loadRcmd(topicId)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        AppLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState,
                            contentPadding = paddingValues
                        ) {
                            when (uiState.tab) {
                                TopicViewModel.Tab.Feed -> RenderTopicList(uiState.feed, onItemClick)
                                TopicViewModel.Tab.Rcmd -> RenderTopicList(uiState.rcmd, onItemClick)
                            }
                        }
                    }
                }
                TextButton(
                    onClick = {
                        viewModel.like(topicId) { ok, err ->
                            MsgUtil.showMsg(if (ok) "已点赞" else err ?: "失败")
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) { Text(stringResource(R.string.topic_like)) }
            }
        }
    }
}

private fun <T> rj.kilikili.ui.components.auto.AppLazyListScope.RenderTopicList(
    list: List<T>,
    onItemClick: (String) -> Unit
) {
    if (list.isEmpty()) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(androidx.compose.ui.res.stringResource(R.string.empty_tip))
            }
        }
    } else {
        items(count = list.size, key = { i -> (list[i] as Any).toString() + i }) { i ->
            val item = list[i]
            val idStr = try {
                val field = item!!::class.java.getDeclaredField("id_str")
                field.isAccessible = true
                field.get(item) as? String ?: ""
            } catch (e: Exception) { "" }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onItemClick(idStr) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = idStr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}