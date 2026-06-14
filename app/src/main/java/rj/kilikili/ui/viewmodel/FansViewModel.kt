package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.RelationExRepository
import com.huanli233.biliwebapi.bean.follow.FollowUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FansUiState {
    data object Loading : FansUiState()
    data class Success(val items: List<FollowUser>) : FansUiState()
    data class Error(val message: String) : FansUiState()
}

@HiltViewModel
class FansViewModel @Inject constructor(
    private val relationExRepository: RelationExRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FansUiState>(FansUiState.Loading)
    val uiState: StateFlow<FansUiState> = _uiState.asStateFlow()

    fun load(mid: Long) {
        viewModelScope.launch {
            _uiState.value = FansUiState.Loading
            relationExRepository.getFans(mid).fold(
                onSuccess = { items -> _uiState.value = FansUiState.Success(items) },
                onFailure = { e -> _uiState.value = FansUiState.Error(e.message ?: "未知错误") }
            )
        }
    }
}