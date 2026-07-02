package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IReserveApi.ReserveInfoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.ReserveRepository
import javax.inject.Inject

@HiltViewModel
class ReserveViewModel @Inject constructor(
    private val repository: ReserveRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val info: ReserveInfoResult? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(reserveId: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getReserveInfo(reserveId).fold(
                onSuccess = { _uiState.value = UiState(info = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun create(title: String, livePlan: Long, upMid: Long, onDone: (Boolean, Long?, String?) -> Unit) {
        viewModelScope.launch {
            repository.createReserve(title, livePlan, upMid).fold(
                onSuccess = { onDone(true, it, null) },
                onFailure = { onDone(false, null, it.message) }
            )
        }
    }

    fun update(reserveId: Long, title: String, livePlan: Long = 0, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.updateReserve(reserveId, title, livePlan).fold(
                onSuccess = { onDone(true, null); load(reserveId) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}