package rj.kilikili.ui.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import rj.kilikili.ui.widget.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.R
import rj.kilikili.actualUiType
import rj.kilikili.UiType
import rj.kilikili.ui.components.freshwear.FreshwearDynamicCard
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.DynamicViewModel
import com.huanli233.biliwebapi.bean.dynamic.Dynamic
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicHomeScreen(
    viewModel: DynamicViewModel = hiltViewModel(),
    onDynamicClick: (Dynamic) -> Unit = {},
    onUserClick: (Long) -> Unit = {},
    onVideoClick: (String) -> Unit = {},
    onImageClick: (List<String>, Int) -> Unit = { _, _ -> },
    onMenuClick: () -> Unit = {}
) {
    val dynamics = viewModel.dynamicFlow.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    val scrollState = rememberAppLazyListState(initialFirstVisibleItemIndex = 0)
    var isRefreshing by remember { mutableStateOf(false) }
    var moreDynamic by remember { mutableStateOf<Dynamic?>(null) }
    var editDynamic by remember { mutableStateOf<Dynamic?>(null) }
    var editContent by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.events.collect { ev ->
            when (ev) {
                is DynamicViewModel.DynamicEvent.Removed -> rj.kilikili.utils.MsgUtil.showMsg("已删除")
                is DynamicViewModel.DynamicEvent.Topped -> rj.kilikili.utils.MsgUtil.showMsg("已置顶")
                is DynamicViewModel.DynamicEvent.Untopped -> rj.kilikili.utils.MsgUtil.showMsg("已取消置顶")
                is DynamicViewModel.DynamicEvent.Edited -> rj.kilikili.utils.MsgUtil.showMsg("已保存")
                is DynamicViewModel.DynamicEvent.Failed -> rj.kilikili.utils.MsgUtil.showMsg(ev.msg)
            }
        }
    }
    
    LaunchedEffect(dynamics.loadState.refresh) {
        if (dynamics.loadState.refresh is LoadState.NotLoading && isRefreshing) {
            isRefreshing = false
        }
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.dynamic),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    dynamics.refresh()
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isRefreshing || (dynamics.loadState.refresh is LoadState.Loading && dynamics.itemCount == 0) -> {
                        LoadingView(
                            state = LoadingState.LOADING,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    dynamics.loadState.refresh is LoadState.Error && dynamics.itemCount == 0 -> {
                        val error = (dynamics.loadState.refresh as LoadState.Error).error
                        LoadingView(
                            state = LoadingState.ERROR,
                            errorMessage = error.stackTraceToString(),
                            onRetry = { dynamics.retry() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    dynamics.itemCount == 0 -> {
                        LoadingView(
                            state = LoadingState.EMPTY,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        AppLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState,
                            contentPadding = paddingValues
                        ) {
                            items(count=dynamics.itemCount) { index ->
                                dynamics[index]?.let { dynamic ->
                                    if (actualUiType == UiType.FRESHWEAR) {
                                        FreshwearDynamicCard(
                                            dynamic = dynamic,
                                            onClick = { onDynamicClick(dynamic) },
                                            onUserClick = onUserClick,
                                            onVideoClick = onVideoClick,
                                            onImageClick = onImageClick,
                                            onLikeClick = { dynamicId, isLiked ->
                                                viewModel.likeDynamic(dynamicId, isLiked)
                                            },
                                            onDynamicClick = onDynamicClick
                                        )
                                    } else {
                                        DynamicCard(
                                            dynamic = dynamic,
                                            onClick = { onDynamicClick(dynamic) },
                                            onUserClick = onUserClick,
                                            onVideoClick = onVideoClick,
                                            onImageClick = onImageClick,
                                            onLikeClick = { dynamicId, isLiked ->
                                                viewModel.likeDynamic(dynamicId, isLiked)
                                            },
                                            onDynamicClick = onDynamicClick,
                                            onMoreClick = { moreDynamic = dynamic }
                                        )
                                    }
                                }
                            }
                            
                            if (dynamics.loadState.append is LoadState.Loading) {
                                item {
                                    LoadingView(
                                        state = LoadingState.LOADING,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    moreDynamic?.let { d ->
        rj.kilikili.ui.dialog.AdaptDialog(
            onDismissRequest = { moreDynamic = null },
            title = { Text(stringResource(R.string.dynamic_more)) },
            text = {
                Column {
                    androidx.compose.material3.TextButton(
                        onClick = { editContent = d.modules.contentModule.desc?.text.orEmpty(); editDynamic = d; moreDynamic = null },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.dynamic_edit), modifier = Modifier.fillMaxWidth()) }
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.setTop(d.id); moreDynamic = null },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.dynamic_set_top), modifier = Modifier.fillMaxWidth()) }
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.removeTop(d.id); moreDynamic = null },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.dynamic_unset_top), modifier = Modifier.fillMaxWidth()) }
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.remove(d.id); moreDynamic = null },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(stringResource(R.string.dynamic_delete), modifier = Modifier.fillMaxWidth()) }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { moreDynamic = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    editDynamic?.let { d ->
        rj.kilikili.ui.dialog.AdaptDialog(
            onDismissRequest = { editDynamic = null },
            title = { Text(stringResource(R.string.dynamic_edit)) },
            text = {
                OutlinedTextField(
                    value = editContent,
                    onValueChange = { editContent = it },
                    label = { Text(stringResource(R.string.dynamic_edit_content)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = { close ->
                TextButton(onClick = {
                    viewModel.edit(d.id, editContent.trim())
                    editDynamic = null
                }) { Text(stringResource(R.string.dynamic_edit_save)) }
            },
            dismissButton = { TextButton(onClick = { editDynamic = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}
