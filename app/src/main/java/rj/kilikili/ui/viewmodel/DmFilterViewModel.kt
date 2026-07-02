package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IDmApi.DmFilterItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.DmFilterRepository
import javax.inject.Inject

@HiltViewModel
class DmFilterViewModel @Inject constructor(
    private val repository: DmFilterRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val filters: List<DmFilterItem> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load() {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getFilters().fold(
                onSuccess = { _uiState.value = UiState(filters = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun add(content: String, type: Int = 1, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.addFilter(content, type).fold(
                onSuccess = { onDone(true, null); load() },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun delete(ids: List<Long>, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.deleteFilters(ids).fold(
                onSuccess = { onDone(true, null); load() },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}