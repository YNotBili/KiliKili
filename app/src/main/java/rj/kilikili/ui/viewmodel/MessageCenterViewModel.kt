package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.MessageRepository
import com.huanli233.biliwebapi.bean.message.UnreadCount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MessageCenterUiState {
    data object Loading : MessageCenterUiState()
    data class Success(val unread: UnreadCount) : MessageCenterUiState()
    data class Error(val message: String) : MessageCenterUiState()
}

@HiltViewModel
class MessageCenterViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MessageCenterUiState>(MessageCenterUiState.Loading)
    val uiState: StateFlow<MessageCenterUiState> = _uiState.asStateFlow()

    init { loadUnreadCount() }

    fun loadUnreadCount() {
        viewModelScope.launch {
            _uiState.value = MessageCenterUiState.Loading
            messageRepository.getUnreadCount().fold(
                onSuccess = { unread ->
                    _uiState.value = MessageCenterUiState.Success(unread)
                },
                onFailure = { error ->
                    _uiState.value = MessageCenterUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}