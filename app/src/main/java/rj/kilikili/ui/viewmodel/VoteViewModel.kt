package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IVoteApi.VoteInfoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.VoteRepository
import javax.inject.Inject

@HiltViewModel
class VoteViewModel @Inject constructor(
    private val repository: VoteRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val info: VoteInfoResult? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(voteId: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getVoteInfo(voteId).fold(
                onSuccess = { _uiState.value = UiState(info = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun cast(voteId: Long, votes: String, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.castVote(voteId, votes).fold(
                onSuccess = { onDone(true, null); load(voteId) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun create(title: String, options: String, onDone: (Boolean, Long?, String?) -> Unit) {
        viewModelScope.launch {
            repository.createVote(title, options).fold(
                onSuccess = { onDone(true, it, null) },
                onFailure = { onDone(false, null, it.message) }
            )
        }
    }
}