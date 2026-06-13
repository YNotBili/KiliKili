package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.MessageRepository
import com.huanli233.biliwebapi.bean.message.ReplyMessageCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReplyMessagesUiState {
    data object Loading : ReplyMessagesUiState()
    data class Success(val items: List<ReplyMessageCard>) : ReplyMessagesUiState()
    data class Error(val message: String) : ReplyMessagesUiState()
}

@HiltViewModel
class ReplyMessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReplyMessagesUiState>(ReplyMessagesUiState.Loading)
    val uiState: StateFlow<ReplyMessagesUiState> = _uiState.asStateFlow()

    private var cursorId: Long = 0
    private var cursorTime: Long = 0
    private var isEnd = false

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ReplyMessagesUiState.Loading
            val result = messageRepository.getReplyMessages()
            result.onSuccess { replyMessagesResult ->
                val items = replyMessagesResult.items ?: emptyList()
                val cursor = replyMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                _uiState.value = ReplyMessagesUiState.Success(items)
            }.onFailure { error ->
                _uiState.value = ReplyMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }

    fun loadMore() {
        if (isEnd) return
        viewModelScope.launch {
            val result = messageRepository.getReplyMessages(id = cursorId, replyTime = cursorTime)
            result.onSuccess { replyMessagesResult ->
                val newItems = replyMessagesResult.items ?: emptyList()
                val cursor = replyMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                val currentItems = (_uiState.value as? ReplyMessagesUiState.Success)?.items ?: emptyList()
                _uiState.value = ReplyMessagesUiState.Success(currentItems + newItems)
            }.onFailure { error ->
                _uiState.value = ReplyMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }
}