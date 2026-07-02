package rj.kilikili.ui.screens.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.live.LiveRoom
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.FollowedLiveViewModel

@Composable
fun FollowedLiveScreen(
    viewModel: FollowedLiveViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onRoomClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(Unit) { viewModel.load() }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.followed_live),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.data == null -> {
                LoadingView(
                    state = LoadingState.LOADING,
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.error != null && uiState.data == null -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            (uiState.data?.list ?: emptyList()).isEmpty() -> {
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
                    items(
                        count = uiState.data?.list?.size ?: 0,
                        key = { i -> uiState.data?.list?.getOrNull(i)?.roomId ?: i }
                    ) { i ->
                        val room = uiState.data!!.list[i]
                        LiveRoomCard(room = room, onClick = { onRoomClick(room.roomId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveRoomCard(room: LiveRoom, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            val cover = room.cover.ifEmpty { room.userCover }
                .ifEmpty { room.showCover }
                .ifEmpty { room.keyframe }
            if (cover.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cover)
                        .crossfade(200)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = room.uname.take(1).ifEmpty { "直播" },
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = room.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${room.uname} · ${room.areaName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "在线 ${room.online}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}