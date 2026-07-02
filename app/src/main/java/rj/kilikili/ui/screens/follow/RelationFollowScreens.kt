package rj.kilikili.ui.screens.follow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rj.kilikili.R
import rj.kilikili.ui.components.UserListScreen
import rj.kilikili.ui.viewmodel.SameFollowingViewModel

@Composable
fun SameFollowingScreen(
    mid: Long,
    viewModel: SameFollowingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onUserClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(mid) { viewModel.load(mid) }

    UserListScreen(
        title = stringResource(R.string.same_following),
        isLoading = uiState.isLoading,
        users = uiState.users,
        error = uiState.error,
        onRetry = { viewModel.load(mid) },
        onNavigateBack = onNavigateBack,
        onUserClick = onUserClick
    )
}

@Composable
fun SearchFollowingScreen(
    mid: Long,
    viewModel: rj.kilikili.ui.viewmodel.SearchFollowingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onUserClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(mid) { viewModel.setMid(mid) }

    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        rj.kilikili.ui.components.SearchBar(
            value = uiState.keyword,
            onValueChange = { viewModel.setKeyword(it) },
            placeholder = stringResource(R.string.search_following_hint)
        )
        UserListScreen(
            title = stringResource(R.string.search_following),
            isLoading = uiState.isLoading,
            users = uiState.users,
            error = uiState.error,
            onRetry = { viewModel.setKeyword(uiState.keyword) },
            onNavigateBack = onNavigateBack,
            onUserClick = onUserClick
        )
    }
}