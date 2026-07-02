package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi.CoinVideoResult
import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi.LikeVideoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.UserSpaceRepository
import javax.inject.Inject

@HiltViewModel
class RecentCoinVideosViewModel @Inject constructor(
    private val repository: UserSpaceRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val data: CoinVideoResult? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(mid: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getRecentCoinVideos(mid).fold(
                onSuccess = { _uiState.value = UiState(data = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}

@HiltViewModel
class RecentLikeVideosViewModel @Inject constructor(
    private val repository: UserSpaceRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val data: LikeVideoResult? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(mid: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getRecentLikeVideos(mid).fold(
                onSuccess = { _uiState.value = UiState(data = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}