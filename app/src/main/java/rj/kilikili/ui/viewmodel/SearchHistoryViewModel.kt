package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IHistoryExApi.HistoryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.HistoryExRepository
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val repository: HistoryExRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val keyword: String = "",
        val results: List<HistoryEntry> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var mid: Long = 0
    private var searchJob: Job? = null

    fun setMid(value: Long) {
        mid = value
    }

    /**
     * Deletes a single history entry by [kid] and removes it from the current results.
     */
    fun removeEntry(kid: Long) {
        viewModelScope.launch {
            repository.deleteHistoryEntry(kid).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        results = _uiState.value.results.filter { it.kid != kid }
                    )
                },
                onFailure = { /* silently ignore – UI will keep the item */ }
            )
        }
    }

    fun setKeyword(keyword: String) {
        _uiState.value = _uiState.value.copy(keyword = keyword)
        searchJob?.cancel()
        if (keyword.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), isLoading = false, error = null)
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            search(keyword)
        }
    }

    private fun search(keyword: String) {
        if (mid == 0L) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.searchHistory(mid, keyword).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, results = it.list) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}