package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.FollowTagRepository
import com.huanli233.biliwebapi.api.interfaces.IFollowTagApi.TagItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FollowTagUiState {
    data object Loading : FollowTagUiState()
    data class Success(val tags: List<TagItem>) : FollowTagUiState()
    data class Error(val message: String) : FollowTagUiState()
}

@HiltViewModel
class FollowTagViewModel @Inject constructor(
    private val followTagRepository: FollowTagRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FollowTagUiState>(FollowTagUiState.Loading)
    val uiState: StateFlow<FollowTagUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = FollowTagUiState.Loading
            followTagRepository.getTags().fold(
                onSuccess = { tags -> _uiState.value = FollowTagUiState.Success(tags) },
                onFailure = { e -> _uiState.value = FollowTagUiState.Error(e.message ?: "未知错误") }
            )
        }
    }
}