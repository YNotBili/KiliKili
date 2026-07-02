package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.ITopicApi.TopicFeedItem
import com.huanli233.biliwebapi.api.interfaces.ITopicApi.TopicRcmdItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.TopicRepository
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val repository: TopicRepository
) : ViewModel() {

    sealed class Tab { data object Feed : Tab(); data object Rcmd : Tab() }

    data class UiState(
        val isLoading: Boolean = false,
        val tab: Tab = Tab.Feed,
        val topicId: Long = 0,
        val feed: List<TopicFeedItem> = emptyList(),
        val rcmd: List<TopicRcmdItem> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setTab(tab: Tab) { _uiState.value = _uiState.value.copy(tab = tab) }

    fun loadFeed(topicId: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(topicId = topicId, isLoading = true, error = null)
        viewModelScope.launch {
            repository.getTopicFeed(topicId).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, feed = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun loadRcmd(topicId: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(topicId = topicId, isLoading = true, error = null)
        viewModelScope.launch {
            repository.getTopicRcmd(topicId).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, rcmd = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun like(topicId: Long, action: Int = 1, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.likeTopic(topicId, action).fold(
                onSuccess = { onDone(true, null) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}