package rj.kilikili.ui.screens.live

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.api.interfaces.ILiveExApi.SuperChatItem
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.SuperChatViewModel

@Composable
fun SuperChatScreen(
    roomId: Long,
    viewModel: SuperChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(roomId) { viewModel.load(roomId) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.super_chat),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            uiState.isLoading && uiState.items.isEmpty() -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            uiState.error != null && uiState.items.isEmpty() -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(roomId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.items.isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues
                ) {
                    items(
                        count = uiState.items.size,
                        key = { i -> "${uiState.items[i].uid}-${uiState.items[i].timestamp}" }
                    ) { i ->
                        SuperChatItemCard(item = uiState.items[i])
                    }
                }
            }
        }
    }
}

@Composable
private fun SuperChatItemCard(item: SuperChatItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${item.uname}  ¥${item.price}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}