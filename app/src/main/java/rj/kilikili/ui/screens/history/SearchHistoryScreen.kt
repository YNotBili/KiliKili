package rj.kilikili.ui.screens.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.rememberRevealState
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.R
import rj.kilikili.ui.components.SearchBar
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.freshwear.FreshwearSwipeToReveal
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.SearchHistoryViewModel

@Composable
fun SearchHistoryScreen(
    mid: Long,
    viewModel: SearchHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    LaunchedEffect(mid) { viewModel.setMid(mid) }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.search_history),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                value = uiState.keyword,
                onValueChange = { viewModel.setKeyword(it) },
                placeholder = stringResource(R.string.search_history_hint)
            )
            when {
                uiState.isLoading && uiState.results.isEmpty() -> {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                }
                uiState.error != null && uiState.results.isEmpty() -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.setKeyword(uiState.keyword) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.keyword.isBlank() -> {
                    Text(
                        text = stringResource(R.string.search_history_hint_tip),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                uiState.results.isEmpty() -> {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                }
                else -> {
                    AppLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState,
                        contentPadding = paddingValues
                    ) {
                        items(
                            count = uiState.results.size,
                            key = { i -> "${uiState.results[i].kid}" }
                        ) { i ->
                            val entry = uiState.results[i]
                            val revealState = key(entry.kid) { rememberRevealState() }
                            FreshwearSwipeToReveal(
                                revealState = revealState,
                                autoClose = false,
                                primaryAction = {
                                    PrimaryActionButton(
                                        onClick = { viewModel.removeEntry(entry.kid) },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "删除"
                                            )
                                        },
                                        text = { Text("删除") }
                                    )
                                },
                                onSwipePrimaryAction = { viewModel.removeEntry(entry.kid) }
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = entry.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
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
        }
    }
}