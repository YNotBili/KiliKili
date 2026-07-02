package rj.kilikili.ui.screens.video

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
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.viewmodel.VoteViewModel
import rj.kilikili.utils.MsgUtil

@Composable
fun VoteScreen(
    voteId: Long,
    viewModel: VoteViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(voteId) { viewModel.load(voteId) }

    AppScreenScaffold(
        topBar = appTopBar(
            title = stringResource(R.string.vote),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading && uiState.info == null -> {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                }
                uiState.error != null && uiState.info == null -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.load(voteId) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.info == null -> {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = uiState.info!!.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "类型 ${uiState.info!!.type} · ID ${uiState.info!!.vote_id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        OutlinedTextField(
                            value = selected,
                            onValueChange = { selected = it },
                            label = { Text("投票选项（选项 ID 列表，逗号分隔）") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (selected.isBlank()) {
                                    MsgUtil.showMsg("请输入选项")
                                    return@Button
                                }
                                viewModel.cast(voteId, selected) { ok, err ->
                                    if (ok) MsgUtil.showMsg("投票成功") else MsgUtil.showMsg(err ?: "失败")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.vote_cast)) }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.vote_create)) }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var title by remember { mutableStateOf("") }
        var options by remember { mutableStateOf("") }
        AdaptDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(stringResource(R.string.vote_create)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("标题") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = options,
                        onValueChange = { options = it },
                        label = { Text("选项（每行一个或 JSON 数组）") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = { close ->
                TextButton(onClick = {
                    if (title.isBlank() || options.isBlank()) { MsgUtil.showMsg("标题和选项不能为空"); return@TextButton }
                    viewModel.create(title.trim(), options.trim()) { ok, id, err ->
                        if (ok) MsgUtil.showMsg("已创建 ID=$id") else MsgUtil.showMsg(err ?: "失败")
                        showCreateDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}