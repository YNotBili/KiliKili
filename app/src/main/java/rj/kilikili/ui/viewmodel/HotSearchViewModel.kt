package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.SearchExRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HotSearchUiState {
    data object Loading : HotSearchUiState()
    data class Success(val keywords: List<String>) : HotSearchUiState()
    data class Error(val message: String) : HotSearchUiState()
}

@HiltViewModel
class HotSearchViewModel @Inject constructor(
    private val searchExRepository: SearchExRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HotSearchUiState>(HotSearchUiState.Loading)
    val uiState: StateFlow<HotSearchUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HotSearchUiState.Loading
            searchExRepository.getTrendingRanking().fold(
                onSuccess = { items ->
                    _uiState.value = HotSearchUiState.Success(items.map { it.keyword })
                },
                onFailure = { e ->
                    _uiState.value = HotSearchUiState.Error(e.message ?: "未知错误")
                }
            )
        }
    }
}