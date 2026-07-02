package rj.kilikili.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.electric.ElectricPanel
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.ElectricPanelViewModel

@Composable
fun ElectricPanelTab(
    mid: Long,
    paddingValues: PaddingValues,
    onUserClick: (Long) -> Unit = {},
    viewModel: ElectricPanelViewModel = hiltViewModel(key = "electric_$mid")
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    LaunchedEffect(mid) { viewModel.load(mid) }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        when {
            uiState.isLoading && uiState.data == null -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            uiState.error != null && uiState.data == null -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(mid) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            (uiState.data?.list ?: emptyList()).isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                ElectricList(
                    panel = uiState.data!!,
                    onUserClick = onUserClick,
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
private fun ElectricList(
    panel: ElectricPanel,
    onUserClick: (Long) -> Unit,
    scrollState: rj.kilikili.ui.components.auto.AppLazyListState
) {
    AppLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "本月充电 ${panel.count} 人",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    if (panel.total > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "累计 ${panel.total} 次",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
        items(
            count = panel.list.size,
            key = { i -> "${panel.list[i].mid}-$i" }
        ) { i ->
            val user = panel.list[i]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onUserClick(user.mid) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${user.rank}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(28.dp)
                    )
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.avatar)
                            .crossfade(200)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.uname,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (user.message.isNotEmpty() && user.msgHidden == 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.message,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}