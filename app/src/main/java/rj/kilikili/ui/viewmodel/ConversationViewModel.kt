package rj.kilikili.ui.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.PrivateMsgRepository
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationUiState(
    val messages: List<PrivateMessage> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val talkerUid: Long = 0,
    val talkerName: String = ""
)

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val privateMsgRepository: PrivateMsgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    fun loadMessages(talkerId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            privateMsgRepository.getMessages(talkerId = talkerId).fold(
                onSuccess = { result ->
                    _uiState.value = _uiState.value.copy(
                        messages = result.messages,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "未知错误"
                    )
                }
            )
        }
    }

    fun sendMessage(content: String, senderUid: Long, talkerId: Long) {
        if (content.isBlank()) return
        viewModelScope.launch {
            privateMsgRepository.sendMessage(
                senderUid = senderUid,
                receiverId = talkerId,
                content = content,
                devId = Build.ID
            ).fold(
                onSuccess = {
                    loadMessages(talkerId)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "发送失败"
                    )
                }
            )
        }
    }

    fun updateAck(talkerId: Long) {
        viewModelScope.launch {
            privateMsgRepository.updateAck(talkerId = talkerId)
        }
    }
}