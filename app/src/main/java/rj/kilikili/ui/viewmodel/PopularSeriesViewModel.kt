package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.UserSpaceRepository
import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi.PopularSeriesItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PopularSeriesUiState {
    data object Loading : PopularSeriesUiState()
    data class Success(val series: List<PopularSeriesItem>) : PopularSeriesUiState()
    data class Error(val message: String) : PopularSeriesUiState()
}

@HiltViewModel
class PopularSeriesViewModel @Inject constructor(
    private val userSpaceRepository: UserSpaceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PopularSeriesUiState>(PopularSeriesUiState.Loading)
    val uiState: StateFlow<PopularSeriesUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PopularSeriesUiState.Loading
            userSpaceRepository.getPopularSeriesList().fold(
                onSuccess = { series -> _uiState.value = PopularSeriesUiState.Success(series) },
                onFailure = { e -> _uiState.value = PopularSeriesUiState.Error(e.message ?: "未知错误") }
            )
        }
    }
}