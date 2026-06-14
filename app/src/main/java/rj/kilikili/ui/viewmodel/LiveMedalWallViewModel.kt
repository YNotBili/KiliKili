package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.LiveExRepository
import com.huanli233.biliwebapi.api.interfaces.ILiveExApi.LiveMedalItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LiveMedalWallUiState {
    data object Loading : LiveMedalWallUiState()
    data class Success(val medals: List<LiveMedalItem>) : LiveMedalWallUiState()
    data class Error(val message: String) : LiveMedalWallUiState()
}

@HiltViewModel
class LiveMedalWallViewModel @Inject constructor(
    private val liveExRepository: LiveExRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LiveMedalWallUiState>(LiveMedalWallUiState.Loading)
    val uiState: StateFlow<LiveMedalWallUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = LiveMedalWallUiState.Loading
            liveExRepository.getMedalWall().fold(
                onSuccess = { r -> _uiState.value = LiveMedalWallUiState.Success(r.list) },
                onFailure = { e -> _uiState.value = LiveMedalWallUiState.Error(e.message ?: "未知错误") }
            )
        }
    }
}