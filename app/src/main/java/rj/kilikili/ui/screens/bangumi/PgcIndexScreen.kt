package rj.kilikili.ui.screens.bangumi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.AppSelector
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.PgcIndexViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PgcIndexScreen(
    viewModel: PgcIndexViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onBangumiClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberAppLazyListState()

    LaunchedEffect(Unit) { viewModel.loadCondition() }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.pgc_index),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        val areaFilter = uiState.filters.firstOrNull { it.field == "area" }
        val seasonVersionFilter = uiState.filters.firstOrNull { it.field == "season_version" }
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            AppSelector(
                options = listOf("" to "全部地区") + (areaFilter?.values ?: emptyList()).map { it.id to it.name },
                selected = uiState.selectedArea,
                onSelected = { viewModel.setArea(it) },
                label = "地区",
                modifier = Modifier.fillMaxWidth()
            )
            AppSelector(
                options = listOf(0 to "全部类型") + (seasonVersionFilter?.values ?: emptyList()).map { (it.id.toIntOrNull() ?: 0) to it.name },
                selected = uiState.selectedSeasonType,
                onSelected = { viewModel.setSeasonType(it) },
                label = "类型",
                modifier = Modifier.fillMaxWidth()
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.list.isEmpty() -> {
                        LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                    }
                    uiState.error != null && uiState.list.isEmpty() -> {
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
                                if (i == uiState.list.lastIndex) {
                                    LaunchedEffect(Unit) { viewModel.loadMore() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BangumiCard(bangumi: BangumiDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(bangumi.media.cover).crossfade(200).build(),
                contentDescription = null,
                modifier = Modifier.size(width = 70.dp, height = 100.dp).clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bangumi.media.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${bangumi.media.typeName} · ${bangumi.media.areas.firstOrNull()?.name ?: ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                bangumi.media.newEp?.let { ne ->
                    Text(
                        text = "更新至 ${ne.indexShow}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}