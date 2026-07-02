package rj.kilikili.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.follow.FollowUser
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView

@Composable
fun UserListScreen(
    title: String,
    isLoading: Boolean,
    users: List<FollowUser>,
    error: String? = null,
    onRetry: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onUserClick: (Long) -> Unit = {}
) {
    val scrollState = rememberAppLazyListState()
    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = title,
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        when {
            isLoading && users.isEmpty() -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            error != null && users.isEmpty() -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = error,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize()
                )
            }
            users.isEmpty() -> {
                LoadingView(state = LoadingState.EMPTY, modifier = Modifier.fillMaxSize())
            }
            else -> {
                AppLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState,
                    contentPadding = paddingValues
                ) {
                    items(
                        count = users.size,
                        key = { i -> users[i].mid }
                    ) { i ->
                        UserCard(user = users[i], onClick = { onUserClick(users[i].mid) })
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(user: FollowUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.face)
                    .crossfade(200)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
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
                if (user.sign.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.sign,
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

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Search,
    onImeAction: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = if (onImeAction != null) {
            androidx.compose.foundation.text.KeyboardActions(onSearch = { onImeAction() })
        } else androidx.compose.foundation.text.KeyboardActions.Default
    )
}