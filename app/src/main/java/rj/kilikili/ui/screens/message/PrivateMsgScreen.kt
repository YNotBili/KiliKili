package rj.kilikili.ui.screens.message

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
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
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
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.PrivateMsgUiState
import rj.kilikili.ui.viewmodel.PrivateMsgViewModel
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMsgSession

@Composable
fun PrivateMsgScreen(
    onSessionClick: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PrivateMsgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.private_messages),
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
            is PrivateMsgUiState.Loading -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            is PrivateMsgUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadSessions() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is PrivateMsgUiState.Success -> {
                if (state.sessions.isEmpty()) {
                    LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
                } else {
                    SessionList(
                        sessions = state.sessions,
                        scrollState = scrollState,
                        paddingValues = paddingValues,
                        onSessionClick = onSessionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionList(
    sessions: List<PrivateMsgSession>,
    scrollState: rj.kilikili.ui.components.auto.AppLazyListState,
    paddingValues: PaddingValues,
    onSessionClick: (Long) -> Unit
) {
    ScalingLazyColumn(
        state = (scrollState as rj.kilikili.ui.components.wear.WearLazyListStateAdapter).delegate,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(sessions, key = { it.talkerUid }) { session ->
            SessionCard(
                session = session,
                onClick = { onSessionClick(session.talkerUid) }
            )
        }
    }
}

@Composable
private fun SessionCard(
    session: PrivateMsgSession,
    onClick: () -> Unit
) {
    val avatarUrl = session.accountInfo?.face.orEmpty()
    val displayName = session.accountInfo?.name ?: "用户 ${session.talkerUid}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (avatarUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(200)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                session.lastMsg?.let { lastMsg ->
                    if (lastMsg.content.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = lastMsg.content,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (session.unread > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = if (session.unread > 99) "99+" else session.unread.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}