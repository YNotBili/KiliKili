package rj.kilikili.ui.screens.live

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.wear.compose.foundation.lazy.items
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
import rj.kilikili.ui.viewmodel.RecommendLiveUiState
import rj.kilikili.ui.viewmodel.RecommendLiveViewModel

@Composable
fun RecommendLiveScreen(
    onRoomClick: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RecommendLiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = "推荐直播",
            showBackIcon = true,
            onBackClick = onNavigateBack,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        when (val state = uiState) {
            is RecommendLiveUiState.Loading -> {
                LoadingView(
                    state = LoadingState.LOADING,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is RecommendLiveUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadRecommend() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is RecommendLiveUiState.Success -> {
                if (state.items.isEmpty()) {
                    LoadingView(
                        state = LoadingState.EMPTY,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    RecommendLiveContent(
                        rooms = state.items,
                        onRoomClick = onRoomClick,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendLiveContent(
    rooms: List<LiveRoom>,
    onRoomClick: (Long) -> Unit,
    paddingValues: PaddingValues
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = rooms.size,
            key = { index -> rooms[index].roomId }
        ) { index ->
            LiveRoomCard(
                room = rooms[index],
                onClick = { onRoomClick(rooms[index].roomId) }
            )
        }
    }
}

@Composable
private fun LiveRoomCard(
    room: LiveRoom,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
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
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(room.face)
                                .crossfade(true)
                                .build(),
                            contentDescription = room.uname,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = room.uname,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(3.dp))

                        Text(
                            text = formatViewerCount(room.online),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun formatViewerCount(count: Int): String {
    return when {
        count >= 10000 -> String.format("%.1f万", count / 10000.0)
        count >= 1000 -> String.format("%.1f千", count / 1000.0)
        else -> count.toString()
    }
}