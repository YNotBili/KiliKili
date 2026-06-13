package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.MessageRepository
import com.huanli233.biliwebapi.bean.message.AtMessageCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AtMessagesUiState {
    data object Loading : AtMessagesUiState()
    data class Success(val items: List<AtMessageCard>) : AtMessagesUiState()
    data class Error(val message: String) : AtMessagesUiState()
}

@HiltViewModel
class AtMessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AtMessagesUiState>(AtMessagesUiState.Loading)
    val uiState: StateFlow<AtMessagesUiState> = _uiState.asStateFlow()

    private var cursorId: Long = 0
    private var cursorTime: Long = 0
    private var isEnd = false

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = AtMessagesUiState.Loading
            val result = messageRepository.getAtMessages()
            result.onSuccess { atMessagesResult ->
                val items = atMessagesResult.items ?: emptyList()
                val cursor = atMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                _uiState.value = AtMessagesUiState.Success(items)
            }.onFailure { error ->
                _uiState.value = AtMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }

    fun loadMore() {
        if (isEnd) return
        viewModelScope.launch {
            val result = messageRepository.getAtMessages(id = cursorId, atTime = cursorTime)
            result.onSuccess { atMessagesResult ->
                val newItems = atMessagesResult.items ?: emptyList()
                val cursor = atMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                val currentItems = (_uiState.value as? AtMessagesUiState.Success)?.items ?: emptyList()
                _uiState.value = AtMessagesUiState.Success(currentItems + newItems)
            }.onFailure { error ->
                _uiState.value = AtMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }
}