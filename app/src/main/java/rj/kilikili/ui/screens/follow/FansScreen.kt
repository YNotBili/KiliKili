package rj.kilikili.ui.screens.follow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ScreenScaffold
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.ui.components.ScrollAwareTopBar
import rj.kilikili.ui.components.rememberEnterAlwaysScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.FansUiState
import rj.kilikili.ui.viewmodel.FansViewModel

@Composable
fun FansScreen(
    mid: Long,
    onUserClick: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScalingLazyListState()
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()

    LaunchedEffect(mid) { viewModel.load(mid) }

    ScreenScaffold(
        scrollState = scrollState,
        topBar = {
            ScrollAwareTopBar(
                title = "粉丝",
                scrollBehavior = scrollBehavior,
                showBackIcon = true,
                showMenuIcon = false,
                onBackClick = onNavigateBack,
                onMenuClick = null
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        when (val state = uiState) {
            is FansUiState.Loading -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            is FansUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.load(mid) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is FansUiState.Success -> {
                if (state.items.isEmpty()) {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                } else {
                    FansList(
                        items = state.items,
                        scrollState = scrollState,
                        paddingValues = paddingValues,
                        onUserClick = onUserClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FansList(
    items: List<com.huanli233.biliwebapi.bean.follow.FollowUser>,
    scrollState: androidx.wear.compose.foundation.lazy.ScalingLazyListState,
    paddingValues: PaddingValues,
    onUserClick: (Long) -> Unit
) {
    ScalingLazyColumn(
        state = scrollState,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items, key = { it.mid }) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = { onUserClick(user.mid) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.face).crossfade(200).build(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.uname,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        if (user.sign.isNotEmpty()) {
                            Text(
                                text = user.sign,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}