package rj.kilikili.ui.screens.download

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.data.download.DownloadDisplayItem
import rj.kilikili.data.download.DownloadStatus
import rj.kilikili.data.download.SourceType
import rj.kilikili.R
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.materialcore.plus
import rj.kilikili.ui.components.rememberEnterAlwaysScrollBehavior
import rj.kilikili.ui.components.scrollAwareTopBar
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.DownloadListViewModel

enum class ContentState {
    EMPTY,
    CONTENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadListScreen(
    onNavigateBack: () -> Unit,
    onPlayLocal: (path: String, title: String) -> Unit = { _, _ -> },
    onMenuClick: () -> Unit = {},
    viewModel: DownloadListViewModel = hiltViewModel()
) {
    val displayItems by viewModel.displayItems.collectAsState()
    var deleteTarget by remember { mutableStateOf<DownloadDisplayItem?>(null) }
    var deleteFile by remember { mutableStateOf(false) }

    val scrollState = rememberScalingLazyListState()
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()

    val contentState = remember(displayItems) {
        if (displayItems.isEmpty()) ContentState.EMPTY else ContentState.CONTENT
    }

    ScreenScaffold(
        scrollState = scrollState,
        topBar = scrollAwareTopBar(
            title = stringResource(id = R.string.download_manager),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Crossfade(
            targetState = contentState,
            animationSpec = tween(durationMillis = 300),
            label = "DownloadListContentStateTransition",
            modifier = Modifier.fillMaxSize()
        ) { state ->
            when (state) {
                ContentState.EMPTY -> {
                    LoadingView(
                        state = LoadingState.EMPTY,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ContentState.CONTENT -> {
                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        state = scrollState,
                        contentPadding = paddingValues.plus(
                            PaddingValues(horizontal = 16.dp)
                        )
                    ) {
                        items(
                            count = displayItems.size,
                            key = { index -> displayItems[index].id }
                        ) { index ->
                            val item = displayItems[index]
                            DownloadTaskItem(
                                item = item,
                                onCancel = if (item.dbId != null) {
                                    { viewModel.cancel(item.dbId) }
                                } else null,
                                onRetry = if (item.dbId != null) {
                                    { viewModel.retry(item.dbId) }
                                } else null,
                                onDelete = { deleteTarget = item },
                                onPlayLocal = onPlayLocal
                            )
                        }
                    }
                }
            }
        }

        deleteTarget?.let { target ->
            AdaptDialog(
                onDismissRequest = {
                    deleteTarget = null
                    deleteFile = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.delete(target, deleteFile)
                            deleteTarget = null
                            deleteFile = false
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            deleteTarget = null
                            deleteFile = false
                        }
                    ) {
                        Text("取消")
                    }
                },
                title = { Text("删除下载") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(target.fileName)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = deleteFile, onCheckedChange = { deleteFile = it })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("同时删除文件")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DownloadTaskItem(
    item: DownloadDisplayItem,
    onPlayLocal: (path: String, title: String) -> Unit = { _, _ -> },
    onCancel: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    val canPlay = item.sourceType == SourceType.SCANNED || item.status == DownloadStatus.SUCCEEDED
    val localPath = item.contentUri

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (canPlay && !localPath.isNullOrBlank()) {
                    Modifier.clickable {
                        onPlayLocal(localPath, item.fileName)
                    }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                ) {
                    val url = item.coverUrl
                    if (!url.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(url)
                                .crossfade(200)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = item.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (item.sourceType) {
                                SourceType.SCANNED -> "本地文件"
                                else -> when (item.status) {
                                    DownloadStatus.ENQUEUED -> "等待中"
                                    DownloadStatus.RUNNING -> "下载中"
                                    DownloadStatus.CANCELED -> "已取消"
                                    DownloadStatus.SUCCEEDED -> "已完成"
                                    DownloadStatus.FAILED -> "下载失败"
                                }
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (item.sourceType != SourceType.SCANNED) {
                            Text(
                                text = "${item.progress}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (item.sourceType == SourceType.SCANNED) {
                            Text(
                                text = "未标记",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (item.sourceType != SourceType.SCANNED && item.status != DownloadStatus.SUCCEEDED) {
                        LinearProgressIndicator(
                            progress = { (item.progress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val needCancel = onCancel != null && (item.status == DownloadStatus.RUNNING || item.status == DownloadStatus.ENQUEUED)
                        val needRetry = onRetry != null && item.status == DownloadStatus.FAILED

                        if (needCancel) {
                            IconButton(onClick = onCancel, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "取消")
                            }
                        }
                        if (needRetry) {
                            IconButton(onClick = onRetry, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "重试")
                            }
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "删除")
                        }
                    }
                }
            }
        }
    }
}