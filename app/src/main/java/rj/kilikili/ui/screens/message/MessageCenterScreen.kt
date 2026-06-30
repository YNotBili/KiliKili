package rj.kilikili.ui.screens.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rj.kilikili.ui.components.auto.AppLazyColumn
import androidx.wear.compose.foundation.lazy.items
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.MessageCenterUiState
import rj.kilikili.ui.viewmodel.MessageCenterViewModel

@Composable
fun MessageCenterScreen(
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onAtClick: () -> Unit,
    onSystemClick: () -> Unit,
    onPrivateMsgClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onMenuClick: () -> Unit = {},
    viewModel: MessageCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.message_center),
                scrollBehavior = scrollBehavior,
                showBackIcon = false,
                showMenuIcon = true,
                onMenuClick = onMenuClick
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        when (val state = uiState) {
            is MessageCenterUiState.Loading -> {
                LoadingView(
                    state = LoadingState.LOADING,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is MessageCenterUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadUnreadCount() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is MessageCenterUiState.Success -> {
                MessageCenterList(
                    unread = state.unread,
                    scrollState = scrollState,
                    paddingValues = paddingValues,
                    onLikeClick = onLikeClick,
                    onReplyClick = onReplyClick,
                    onAtClick = onAtClick,
                    onSystemClick = onSystemClick,
                    onPrivateMsgClick = onPrivateMsgClick
                )
            }
        }
    }
}

private data class MessageMenuItem(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int,
    val onClick: () -> Unit
)

@Composable
private fun MessageCenterList(
    unread: com.huanli233.biliwebapi.bean.message.UnreadCount,
    scrollState: rj.kilikili.ui.components.auto.AppLazyListState,
    paddingValues: PaddingValues,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onAtClick: () -> Unit,
    onSystemClick: () -> Unit,
    onPrivateMsgClick: () -> Unit
) {
    val items = listOf(
        MessageMenuItem("点赞", Icons.Default.Favorite, unread.like, onLikeClick),
        MessageMenuItem("回复", Icons.Default.Forum, unread.reply, onReplyClick),
        MessageMenuItem("@我", Icons.Default.AlternateEmail, unread.at, onAtClick),
        MessageMenuItem("系统通知", Icons.Default.Notifications, unread.system, onSystemClick),
    )

    AppLazyColumn(
        state = scrollState,
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            PrivateMsgCard(onClick = onPrivateMsgClick)
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(items, key = { it.title }) { item ->
            MessageCard(item = item)
        }
    }
}

@Composable
private fun PrivateMsgCard(onClick: () -> Unit) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "私信",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MessageCard(item: MessageMenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = item.onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            if (item.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Text(
                            text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}