package rj.kilikili.ui.screens.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.FavoriteUiState
import rj.kilikili.ui.viewmodel.FavoriteViewModel
import com.huanli233.biliwebapi.bean.favorite.FavoriteBox
import kotlinx.coroutines.launch
import rj.kilikili.data.setting.LocalData
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import com.valentinilk.shimmer.shimmer

@Composable
fun FavoriteScreen(
    onFolderClick: (Long, String) -> Unit,
    onOpusFavoriteClick: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scope = rememberCoroutineScope()

    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberPullToRefreshState()
    var moreFolder by remember { mutableStateOf<Pair<Long, String>?>(null) }
    var renameTarget by remember { mutableStateOf<Pair<Long, String>?>(null) }
    var showNewDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Pair<Long, String>?>(null) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.favorite),
                showBackIcon = true,
                showMenuIcon = false,
                onBackClick = onNavigateBack,
                onMenuClick = null
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            state = swipeRefreshState,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.loadFavoriteFolders()
                    isRefreshing = false
                }
            },
            isRefreshing = isRefreshing,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is FavoriteUiState.Loading -> {
                    LoadingView(
                        state = LoadingState.LOADING,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is FavoriteUiState.Error -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = state.message,
                        onRetry = { viewModel.loadFavoriteFolders() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is FavoriteUiState.Success -> {
                    if (state.folders.isEmpty()) {
                        LoadingView(
                            state = LoadingState.EMPTY,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        FavoriteFolderList(
                            folders = state.folders,
                            scrollState = scrollState,
                            paddingValues = paddingValues,
                            onFolderClick = onFolderClick,
                            onOpusFavoriteClick = onOpusFavoriteClick,
                            onMoreClick = { fid, name -> moreFolder = fid to name }
                        )
                    }
                }
            }
            }

            FloatingActionButton(
                onClick = { showNewDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    }

    moreFolder?.let { (fid, name) ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { moreFolder = null },
            title = { Text(name) },
            text = {
                Column {
                    androidx.compose.material3.TextButton(onClick = { renameTarget = fid to name; moreFolder = null }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.favorite_folder_rename), modifier = Modifier.fillMaxWidth())
                    }
                    androidx.compose.material3.TextButton(onClick = { deleteTarget = fid to name; moreFolder = null }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.favorite_folder_delete), modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {},
            dismissButton = { androidx.compose.material3.TextButton(onClick = { moreFolder = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    renameTarget?.let { (fid, oldName) ->
        var newName by remember(fid) { mutableStateOf(oldName) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text(stringResource(R.string.favorite_folder_rename)) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (newName.isBlank()) { rj.kilikili.utils.MsgUtil.showMsg("名称不能为空"); return@TextButton }
                    viewModel.renameFolder(fid, newName.trim()) { ok, err ->
                        rj.kilikili.utils.MsgUtil.showMsg(if (ok) "已重命名" else err ?: "失败")
                        if (ok) renameTarget = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { androidx.compose.material3.TextButton(onClick = { renameTarget = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    deleteTarget?.let { (fid, name) ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(stringResource(R.string.favorite_folder_delete)) },
            text = { Text(stringResource(R.string.favorite_folder_delete_confirm)) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.deleteFolder(fid) { ok, err ->
                        rj.kilikili.utils.MsgUtil.showMsg(if (ok) "已删除" else err ?: "失败")
                        deleteTarget = null
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { androidx.compose.material3.TextButton(onClick = { deleteTarget = null }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    if (showNewDialog) {
        var newName by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNewDialog = false },
            title = { Text(stringResource(R.string.favorite_folder_new)) },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (newName.isBlank()) { rj.kilikili.utils.MsgUtil.showMsg("名称不能为空"); return@TextButton }
                    viewModel.createFolder(newName.trim()) { ok, err ->
                        rj.kilikili.utils.MsgUtil.showMsg(if (ok) "已创建" else err ?: "失败")
                        if (ok) showNewDialog = false
                    }
                }) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = { androidx.compose.material3.TextButton(onClick = { showNewDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

@Composable
private fun FavoriteFolderList(
    folders: List<FavoriteBox>,
    scrollState: rj.kilikili.ui.components.auto.AppLazyListState,
    paddingValues: PaddingValues,
    onFolderClick: (Long, String) -> Unit,
    onOpusFavoriteClick: () -> Unit,
    onMoreClick: (Long, String) -> Unit
) {
    AppLazyColumn(
        state = scrollState,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data=folders, key = { it.favBox }) { folder ->
            FavoriteFolderCard(
                folder = folder,
                onClick = { onFolderClick(folder.favBox, folder.name) },
                onMoreClick = { onMoreClick(folder.favBox, folder.name) }
            )
        }
        
        item {
            OpusFavoriteCard(onClick = onOpusFavoriteClick)
        }
    }
}

@Composable
private fun FavoriteFolderCard(
    folder: FavoriteBox,
    onClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val settings by LocalData.settingsStateFlow.collectAsState()
    val useBackgroundStyle = settings?.uiSettings?.favoriteFolderCardBackgroundStyle ?: false
    val coverUrl = folder.videos?.firstOrNull()?.pic ?: ""
    
    if (useBackgroundStyle && coverUrl.isNotEmpty()) {
        FavoriteFolderCardWithBackground(
            folder = folder,
            coverUrl = coverUrl,
            onClick = onClick
        )
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (coverUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(coverUrl)
                            .crossfade(200)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${folder.count}/${folder.maxCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.dynamic_more)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteFolderCardWithBackground(
    folder: FavoriteBox,
    coverUrl: String,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(90.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverUrl)
                    .crossfade(200)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                onSuccess = { isLoading = false },
                onError = { isLoading = false }
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmer()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "${folder.count}/${folder.maxCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun OpusFavoriteCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Article,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = stringResource(R.string.opus_favorite),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
