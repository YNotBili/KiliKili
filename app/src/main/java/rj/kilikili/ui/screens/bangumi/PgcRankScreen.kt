package rj.kilikili.ui.screens.bangumi

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.PgcIndexViewModel
import rj.kilikili.ui.viewmodel.PgcRankViewModel

@Composable
fun PgcRankScreen(
    viewModel: PgcRankViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onBangumiClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.pgc_rank),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = uiState.seasonType == 1, onClick = { viewModel.setSeasonType(1) }, label = { Text("番剧") })
                FilterChip(selected = uiState.seasonType == 2, onClick = { viewModel.setSeasonType(2) }, label = { Text("国创") })
                FilterChip(selected = uiState.seasonType == 3, onClick = { viewModel.setSeasonType(3) }, label = { Text("电影") })
                FilterChip(selected = uiState.seasonType == 4, onClick = { viewModel.setSeasonType(4) }, label = { Text("纪录片") })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = uiState.day == 1, onClick = { viewModel.setDay(1) }, label = { Text("日榜") })
                FilterChip(selected = uiState.day == 3, onClick = { viewModel.setDay(3) }, label = { Text("三日榜") })
                FilterChip(selected = uiState.day == 7, onClick = { viewModel.setDay(7) }, label = { Text("周榜") })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                    }
                    uiState.error != null -> {
                        LoadingView(
                            state = LoadingState.ERROR,
                            errorMessage = uiState.error,
                            onRetry = { viewModel.refresh() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    uiState.list.isEmpty() -> {
                        LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                    }
                    else -> {
                        AppLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = scrollState
                        ) {
                            items(
                                count = uiState.list.size,
                                key = { i -> uiState.list[i].media.mediaId }
                            ) { i ->
                                val b = uiState.list[i]
                                BangumiCard(bangumi = b, onClick = { onBangumiClick(b.media.seasonId) })
                            }
                        }
                    }
                }
            }
        }
    }
}