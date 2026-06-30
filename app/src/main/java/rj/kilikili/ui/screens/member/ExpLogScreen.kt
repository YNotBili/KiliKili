package rj.kilikili.ui.screens.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.ExpLogUiState
import rj.kilikili.ui.viewmodel.ExpLogViewModel
import com.huanli233.biliwebapi.bean.member.ExpLog

@Composable
fun ExpLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.exp_log),
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
            is ExpLogUiState.Loading -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            is ExpLogUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadExpLog() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is ExpLogUiState.Success -> {
                if (state.logs.isEmpty()) {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                } else {
                    ExpLogList(
                        logs = state.logs,
                        scrollState = scrollState,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpLogList(
    logs: List<ExpLog>,
    scrollState: rj.kilikili.ui.components.auto.AppLazyListState,
    paddingValues: PaddingValues
) {
    ScalingLazyColumn(
        state = (scrollState as rj.kilikili.ui.components.wear.WearLazyListStateAdapter).delegate,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(logs, key = { "${it.time}_${it.delta}_${it.reason.hashCode()}" }) { log ->
            ExpLogCard(log = log)
        }
    }
}

@Composable
private fun ExpLogCard(log: ExpLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.reason,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "+${log.delta}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}