package rj.kilikili.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CardMembership
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import coil3.compose.AsyncImage
import rj.kilikili.R
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView

@Composable
fun MySpaceScreen(
    onNavigateToUserProfile: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToWatchLater: () -> Unit,
    onNavigateToFavorite: () -> Unit,
    onNavigateToFollowing: () -> Unit,
    onNavigateToVipCenter: () -> Unit = {},
    onNavigateToCoinLog: () -> Unit = {},
    onNavigateToExpLog: () -> Unit = {},
    onNavigateToLiveMedal: () -> Unit = {},
    onNavigateToFollowTags: () -> Unit = {},
    onMenuClick: () -> Unit,
    viewModel: MySpaceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState(initialFirstVisibleItemIndex = 0)

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.my_space),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick
        )
    ) { paddingValues ->
        when (val state = uiState) {
            is MySpaceUiState.Loading -> {
                LoadingView(
                    state = LoadingState.LOADING,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is MySpaceUiState.Success -> {
                val menuItems = listOf(
                    MenuItemData(
                        icon = Icons.Default.History,
                        title = stringResource(R.string.history),
                        onClick = onNavigateToHistory
                    ),
                    MenuItemData(
                        icon = Icons.Default.WatchLater,
                        title = stringResource(R.string.watch_later),
                        onClick = onNavigateToWatchLater
                    ),
                    MenuItemData(
                        icon = Icons.Default.Star,
                        title = stringResource(R.string.favorite),
                        onClick = onNavigateToFavorite
                    ),
                    MenuItemData(
                        icon = Icons.Default.People,
                        title = stringResource(R.string.following),
                        onClick = onNavigateToFollowing
                    ),
                    MenuItemData(
                        icon = Icons.Outlined.CardMembership,
                        title = stringResource(R.string.vip_center),
                        onClick = onNavigateToVipCenter
                    ),
                    MenuItemData(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = stringResource(R.string.coin_log),
                        onClick = onNavigateToCoinLog
                    ),
                    MenuItemData(
                        icon = Icons.Outlined.TrendingUp,
                        title = stringResource(R.string.exp_log),
                        onClick = onNavigateToExpLog
                    ),
                    MenuItemData(
                        icon = Icons.Outlined.Favorite,
                        title = stringResource(R.string.live_medal_wall),
                        onClick = onNavigateToLiveMedal
                    ),
                    MenuItemData(
                        icon = Icons.Outlined.Bookmark,
                        title = stringResource(R.string.follow_tags),
                        onClick = onNavigateToFollowTags
                    )
                )
                
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    item {
                        UserInfoCard(
                            navUserInfo = state.navUserInfo,
                            onClick = { onNavigateToUserProfile(state.navUserInfo.mid) }
                        )
                    }

                    items(menuItems) { menuItem ->
                        MenuItem(
                            icon = menuItem.icon,
                            title = menuItem.title,
                            onClick = menuItem.onClick
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            is MySpaceUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadUserInfo() }
                )
            }
        }
    }
}

private data class MenuItemData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val onClick: () -> Unit
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserInfoCard(
    navUserInfo: com.huanli233.biliwebapi.bean.user.NavUserInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = navUserInfo.face,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = navUserInfo.uname,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${navUserInfo.money.toInt()} 硬币",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "LV${navUserInfo.levelInfo.currentLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}
