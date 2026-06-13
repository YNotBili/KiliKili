package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.MessageRepository
import com.huanli233.biliwebapi.bean.message.MessageCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LikeMessagesUiState {
    data object Loading : LikeMessagesUiState()
    data class Success(val items: List<MessageCard>) : LikeMessagesUiState()
    data class Error(val message: String) : LikeMessagesUiState()
}

@HiltViewModel
class LikeMessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LikeMessagesUiState>(LikeMessagesUiState.Loading)
    val uiState: StateFlow<LikeMessagesUiState> = _uiState.asStateFlow()

    private var cursorId: Long = 0
    private var cursorTime: Long = 0
    private var isEnd = false

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = LikeMessagesUiState.Loading
            val result = messageRepository.getLikeMessages()
            result.onSuccess { likeMessagesResult ->
                val items = likeMessagesResult.total?.items ?: emptyList()
                val cursor = likeMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                _uiState.value = LikeMessagesUiState.Success(items)
            }.onFailure { error ->
                _uiState.value = LikeMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }

    fun loadMore() {
        if (isEnd) return
        viewModelScope.launch {
            val result = messageRepository.getLikeMessages(id = cursorId, replyTime = cursorTime)
            result.onSuccess { likeMessagesResult ->
                val newItems = likeMessagesResult.total?.items ?: emptyList()
                val cursor = likeMessagesResult.cursor
                if (cursor != null) {
                    cursorId = cursor.id
                    cursorTime = cursor.time
                    isEnd = cursor.isEnd
                }
                val currentItems = (_uiState.value as? LikeMessagesUiState.Success)?.items ?: emptyList()
                _uiState.value = LikeMessagesUiState.Success(currentItems + newItems)
            }.onFailure { error ->
                _uiState.value = LikeMessagesUiState.Error(error.message ?: "未知错误")
            }
        }
    }
}