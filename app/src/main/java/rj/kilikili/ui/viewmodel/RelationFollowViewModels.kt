package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.bean.follow.FollowUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.RelationExRepository
import javax.inject.Inject

@HiltViewModel
class SameFollowingViewModel @Inject constructor(
    private val repository: RelationExRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val users: List<FollowUser> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(mid: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getSameFollowing(mid).fold(
                onSuccess = { _uiState.value = UiState(users = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}

@HiltViewModel
class SearchFollowingViewModel @Inject constructor(
    private val repository: RelationExRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val keyword: String = "",
        val users: List<FollowUser> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var mid: Long = 0
    private var searchJob: Job? = null

    fun setMid(value: Long) {
        mid = value
        if (_uiState.value.keyword.isNotEmpty()) search(_uiState.value.keyword)
    }

    fun setKeyword(keyword: String) {
        _uiState.value = _uiState.value.copy(keyword = keyword)
        searchJob?.cancel()
        if (keyword.isBlank()) {
            _uiState.value = _uiState.value.copy(users = emptyList(), isLoading = false, error = null)
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
            repository.searchFollowing(mid, keyword).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, users = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}