package rj.kilikili.ui.screens.bangumi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.PgcReviewViewModel
import rj.kilikili.utils.MsgUtil

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PgcReviewScreen(
    mediaId: Long,
    viewModel: PgcReviewViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()
    var showWriteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(mediaId, uiState.tab) { viewModel.load(mediaId) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.pgc_review),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = if (uiState.tab == PgcReviewViewModel.Tab.Short) 0 else 1) {
                Tab(
                    selected = uiState.tab == PgcReviewViewModel.Tab.Short,
                    onClick = { viewModel.setTab(PgcReviewViewModel.Tab.Short) },
                    text = { Text(stringResource(R.string.pgc_review_short)) }
                )
                Tab(
                    selected = uiState.tab == PgcReviewViewModel.Tab.Long,
                    onClick = { viewModel.setTab(PgcReviewViewModel.Tab.Long) },
                    text = { Text(stringResource(R.string.pgc_review_long)) }
                )
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                    uiState.error != null -> LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.load(mediaId) },
                        modifier = Modifier.fillMaxSize()
                    )
                    uiState.items.isEmpty() -> LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                    else -> {
                        AppLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState,
                            contentPadding = paddingValues
                        ) {
                            items(
                                count = uiState.items.size,
                                key = { i -> uiState.items[i].id }
                            ) { i ->
                                val r = uiState.items[i]
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            r.user?.let { u ->
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current).data(u.avatar).crossfade(200).build(),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(text = u.uname, style = MaterialTheme.typography.labelMedium)
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = "★ ${r.score}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = r.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 6,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        TextButton(onClick = {
                                            viewModel.like(r.id) { ok, err ->
                                                MsgUtil.showMsg(if (ok) "已点赞" else err ?: "失败")
                                            }
                                        }) {
                                            Icon(Icons.Filled.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("${r.like_count}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (uiState.tab == PgcReviewViewModel.Tab.Short) {
                    FloatingActionButton(
                        onClick = { showWriteDialog = true },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) {
                        Text(stringResource(R.string.pgc_review_write))
                    }
                }
            }
        }
    }

    if (showWriteDialog) {
        var content by remember { mutableStateOf("") }
        var score by remember { mutableStateOf(8) }
        AdaptDialog(
            onDismissRequest = { showWriteDialog = false },
            title = { Text(stringResource(R.string.pgc_review_write)) },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("评分 $score")
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.IconButton(
                            onClick = { if (score > 1) score-- },
                            enabled = score > 1
                        ) { Text("-") }
                        androidx.compose.material3.IconButton(
                            onClick = { if (score < 10) score++ },
                            enabled = score < 10
                        ) { Text("+") }
                    }
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("短评内容") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = { close ->
                TextButton(onClick = {
                    if (content.isBlank()) { MsgUtil.showMsg("内容不能为空"); return@TextButton }
                    viewModel.postShort(mediaId, content.trim(), score) { ok, err ->
                        MsgUtil.showMsg(if (ok) "已发布" else err ?: "失败")
                        if (ok) showWriteDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { showWriteDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}