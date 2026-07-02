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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huanli233.biliwebapi.api.interfaces.INoteApi.NoteItem
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.NoteViewModel
import rj.kilikili.utils.MsgUtil

@Composable
fun NoteListScreen(
    oid: Long,
    viewModel: NoteViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<NoteItem?>(null) }

    LaunchedEffect(oid) { viewModel.loadForArchive(oid) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.video_note),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.notes.isEmpty() -> {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                }
                uiState.error != null && uiState.notes.isEmpty() -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.loadForArchive(oid) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.notes.isEmpty() -> {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                }
                else -> {
                    AppLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = paddingValues
                    ) {
                        items(
                            count = uiState.notes.size,
                            key = { i -> uiState.notes[i].note_id }
                        ) { i ->
                            NoteCard(
                                note = uiState.notes[i],
                                onDelete = { pendingDelete = uiState.notes[i] }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "添加笔记")
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        AdaptDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.video_note_add)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.video_note_title)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(stringResource(R.string.video_note_content)) },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = { close ->
                TextButton(onClick = {
                    if (title.isBlank() || content.isBlank()) { MsgUtil.showMsg("标题和内容不能为空"); return@TextButton }
                    viewModel.add(oid, title.trim(), content.trim()) { ok, err ->
                        if (ok) MsgUtil.showMsg("已添加") else MsgUtil.showMsg(err ?: "失败")
                        showAddDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    pendingDelete?.let { note ->
        AdaptDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.video_note_delete)) },
            text = { Text(stringResource(R.string.video_note_delete_confirm)) },
            confirmButton = { close ->
                TextButton(onClick = {
                    viewModel.delete(note.note_id, oid) { ok, err ->
                        if (ok) MsgUtil.showMsg("已删除") else MsgUtil.showMsg(err ?: "失败")
                        pendingDelete = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@Composable
private fun NoteCard(note: NoteItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "删除")
                }
            }
            if (note.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}