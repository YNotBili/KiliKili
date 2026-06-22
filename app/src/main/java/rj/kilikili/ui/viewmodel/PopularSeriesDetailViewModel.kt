package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.UserSpaceRepository
import com.huanli233.biliwebapi.bean.video.VideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PopularSeriesDetailUiState {
    data object Loading : PopularSeriesDetailUiState()
    data class Success(val videos: List<VideoInfo>) : PopularSeriesDetailUiState()
    data class Error(val message: String) : PopularSeriesDetailUiState()
}

@HiltViewModel
class PopularSeriesDetailViewModel @Inject constructor(
    private val userSpaceRepository: UserSpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PopularSeriesDetailUiState>(PopularSeriesDetailUiState.Loading)
    val uiState: StateFlow<PopularSeriesDetailUiState> = _uiState.asStateFlow()

    fun load(seriesId: Int) {
        viewModelScope.launch {
            _uiState.value = PopularSeriesDetailUiState.Loading
            userSpaceRepository.getPopularSeriesDetail(seriesId).fold(
                onSuccess = { videos ->
                    _uiState.value = PopularSeriesDetailUiState.Success(videos)
                },
                onFailure = { e ->
                    _uiState.value = PopularSeriesDetailUiState.Error(e.message ?: "未知错误")
                }
            )
        }
    }
}
