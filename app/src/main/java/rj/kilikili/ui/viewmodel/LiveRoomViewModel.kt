package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.ILiveApi
import com.huanli233.biliwebapi.bean.live.LiveRoom
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LiveRoomUiState {
    data object Loading : LiveRoomUiState()
    data class Success(val room: LiveRoom) : LiveRoomUiState()
    data class Error(val message: String) : LiveRoomUiState()
}

@HiltViewModel
class LiveRoomViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<LiveRoomUiState>(LiveRoomUiState.Loading)
    val uiState: StateFlow<LiveRoomUiState> = _uiState.asStateFlow()

    fun loadRoomInfo(roomId: Long) {
        viewModelScope.launch {
            _uiState.value = LiveRoomUiState.Loading
            bilibiliApi.api(ILiveApi::class) {
                getRoomInfo(roomId.toString())
            }.apiResultNonNull().fold(
                onSuccess = { _uiState.value = LiveRoomUiState.Success(it) },
                onFailure = { _uiState.value = LiveRoomUiState.Error(it.message ?: "error") }
            )
        }
    }
}