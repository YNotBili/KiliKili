package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.bilizepam.data.repository.BangumiRepository
import com.huanli233.biliwebapi.api.interfaces.IBangumiApi
import com.huanli233.biliwebapi.api.interfaces.IBangumiApi.FollowedBangumiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FollowingBangumiUiState {
    data object Loading : FollowingBangumiUiState()
    data class Success(val items: List<FollowedBangumiItem>) : FollowingBangumiUiState()
    data class Error(val message: String) : FollowingBangumiUiState()
}

@HiltViewModel
class FollowingBangumiViewModel @Inject constructor(
    private val bangumiRepository: BangumiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FollowingBangumiUiState>(FollowingBangumiUiState.Loading)
    val uiState: StateFlow<FollowingBangumiUiState> = _uiState.asStateFlow()

    init { loadBangumiList() }

    fun loadBangumiList(page: Int = 1) {
        viewModelScope.launch {
            _uiState.value = FollowingBangumiUiState.Loading
            bangumiRepository.getFollowedBangumi(page = page).fold(
                onSuccess = { items ->
                    _uiState.value = FollowingBangumiUiState.Success(items)
                },
                onFailure = { error ->
                    _uiState.value = FollowingBangumiUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}
