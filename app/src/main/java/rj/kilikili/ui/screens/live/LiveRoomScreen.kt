package rj.kilikili.ui.screens.live

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.live.LiveRoom
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.LiveRoomUiState
import rj.kilikili.ui.viewmodel.LiveRoomViewModel

@Composable
fun LiveRoomScreen(
    roomId: Long,
    onNavigateBack: () -> Unit,
    viewModel: LiveRoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    LaunchedEffect(roomId) {
        viewModel.loadRoomInfo(roomId)
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = "直播",
            showBackIcon = true,
            onBackClick = onNavigateBack,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        when (val state = uiState) {
            is LiveRoomUiState.Loading -> {
                LoadingView(
                    state = LoadingState.LOADING,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is LiveRoomUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadRoomInfo(roomId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is LiveRoomUiState.Success -> {
                LiveRoomContent(
                    room = state.room,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
private fun LiveRoomContent(
    room: LiveRoom,
    paddingValues: PaddingValues
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            LiveRoomHeader(room = room)
        }
        item {
            LiveRoomInfoCard(room = room)
        }
        item {
            LiveRoomStatusCard(room = room)
        }
        item {
            LiveRoomDescriptionCard(room = room)
        }
    }
}

@Composable
private fun LiveRoomHeader(room: LiveRoom) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(room.cover.ifEmpty { room.userCover })
                .crossfade(true)
                .build(),
            contentDescription = room.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = room.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LiveRoomInfoCard(room: LiveRoom) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(room.face)
                    .crossfade(true)
                    .build(),
                contentDescription = room.uname,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.uname,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = room.areaName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LiveRoomStatusCard(room: LiveRoom) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "直播状态",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (room.liveStatus == 1) "直播中" else "未开播",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (room.liveStatus == 1)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "观看人数",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = room.online.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LiveRoomDescriptionCard(room: LiveRoom) {
    if (room.description.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "简介",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}