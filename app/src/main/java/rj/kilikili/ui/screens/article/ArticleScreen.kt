package rj.kilikili.ui.screens.article

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.ArticleViewModel

@Composable
fun ArticleScreen(
    cvid: Long,
    viewModel: ArticleViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    LaunchedEffect(cvid) { viewModel.load(cvid) }

    AppScreenScaffold(
        topBar = appTopBar(
            title = stringResource(R.string.article),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                uiState.error != null -> LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = uiState.error,
                    onRetry = { viewModel.load(cvid) },
                    modifier = Modifier.fillMaxSize()
                )
                uiState.article == null -> LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                else -> {
                    val a = uiState.article!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        a.cover?.takeIf { it.isNotBlank() }?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(it).crossfade(200).build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        Text(
                            text = a.title.orEmpty(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        if (!a.authorName.isNullOrBlank()) {
                            Text(
                                text = a.authorName.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        a.stats?.let { s ->
                            Text(
                                text = "阅读 ${s.view}  ·  点赞 ${s.like}  ·  评论 ${s.reply}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        a.content?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}