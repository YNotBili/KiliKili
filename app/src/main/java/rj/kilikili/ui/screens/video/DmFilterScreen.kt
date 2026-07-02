package rj.kilikili.ui.screens.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.utils.MsgUtil
import rj.kilikili.ui.viewmodel.DmFilterViewModel

@Composable
fun DmFilterScreen(
    viewModel: DmFilterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) { viewModel.load() }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.danmaku_filter),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.filters.isEmpty() -> {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                }
                uiState.error != null && uiState.filters.isEmpty() -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.load() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.filters.isEmpty() -> {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                }
                else -> {
                    AppLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = paddingValues
                    ) {
                        items(
                            count = uiState.filters.size,
                            key = { i -> uiState.filters[i].id }
                        ) { i ->
                            val f = uiState.filters[i]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = f.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = filterTypeName(f.type),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { pendingDeleteId = f.id }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "删除")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加")
            }
        }
    }

    if (showAddDialog) {
        var content by remember { mutableStateOf("") }
        var type by remember { mutableStateOf(1) }
        AdaptDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.danmaku_filter_add)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(stringResource(R.string.danmaku_filter_content)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.danmaku_filter_type))
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(selected = type == 1, onClick = { type = 1 }, label = { Text("关键字") })
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(selected = type == 2, onClick = { type = 2 }, label = { Text("正则") })
                        Spacer(modifier = Modifier.width(4.dp))
                        FilterChip(selected = type == 3, onClick = { type = 3 }, label = { Text("用户") })
                    }
                }
            },
            confirmButton = { close ->
                TextButton(onClick = {
                    if (content.isBlank()) { MsgUtil.showMsg("内容不能为空"); return@TextButton }
                    viewModel.add(content.trim(), type) { ok, err ->
                        if (ok) MsgUtil.showMsg("已添加") else MsgUtil.showMsg(err ?: "失败")
                        showAddDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    pendingDeleteId?.let { id ->
        AdaptDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(stringResource(R.string.danmaku_filter_delete)) },
            text = { Text(stringResource(R.string.danmaku_filter_delete_confirm)) },
            confirmButton = { close ->
                TextButton(onClick = {
                    viewModel.delete(listOf(id)) { ok, err ->
                        if (ok) MsgUtil.showMsg("已删除") else MsgUtil.showMsg(err ?: "失败")
                        pendingDeleteId = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

private fun filterTypeName(type: Int): String = when (type) {
    1 -> "关键字"
    2 -> "正则"
    3 -> "用户"
    else -> "其他"
}