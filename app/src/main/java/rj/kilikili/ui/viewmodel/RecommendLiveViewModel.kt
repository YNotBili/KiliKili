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

sealed class RecommendLiveUiState {
    data object Loading : RecommendLiveUiState()
    data class Success(val items: List<LiveRoom>) : RecommendLiveUiState()
    data class Error(val message: String) : RecommendLiveUiState()
}

@HiltViewModel
class RecommendLiveViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RecommendLiveUiState>(RecommendLiveUiState.Loading)
    val uiState: StateFlow<RecommendLiveUiState> = _uiState.asStateFlow()

    init { loadRecommend() }

    fun loadRecommend() {
        viewModelScope.launch {
            _uiState.value = RecommendLiveUiState.Loading
            // ILiveApi.getRecommendLive() returns ApiResponse<RecommendLiveData>
            // RecommendLiveData.roomList: List<LiveRoom>
            bilibiliApi.api(ILiveApi::class) {
                getRecommendLive()
            }.apiResultNonNull().fold(
                onSuccess = { data ->
                    _uiState.value = RecommendLiveUiState.Success(data.roomList)
                },
                onFailure = { error ->
                    _uiState.value = RecommendLiveUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}