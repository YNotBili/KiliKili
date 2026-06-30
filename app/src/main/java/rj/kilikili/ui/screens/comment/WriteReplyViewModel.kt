package rj.kilikili.ui.screens.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.api.BilibiliApiException
import rj.kilikili.data.repository.ReplyRepository
import rj.kilikili.utils.MsgUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WriteReplyUiState(
    val isSending: Boolean = false,
    val error: String? = null
)

sealed class WriteReplyEvent {
    data object Success : WriteReplyEvent()
    data class Error(val message: String) : WriteReplyEvent()
}

internal val writeReplyErrorMessages: Map<Int, String> = mapOf(
    -101 to "没有登录或登录信息有误",
    -102 to "账号被封禁",
    -509 to "请求过于频繁",
    12015 to "需要评论验证码",
    12016 to "包含敏感内容",
    12025 to "字数过多",
    12035 to "被拉黑了",
    12051 to "重复评论，请勿刷屏"
)

internal fun resolveWriteReplyError(error: Throwable): String {
    val apiCode = (error as? BilibiliApiException)?.code
    if (apiCode != null) {
        writeReplyErrorMessages[apiCode]?.let { return it }
    }
    return error.message ?: "发送失败，请重试"
}

@HiltViewModel
class WriteReplyViewModel @Inject constructor(
    private val replyRepository: ReplyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteReplyUiState())
    val uiState: StateFlow<WriteReplyUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WriteReplyEvent>()
    val events: SharedFlow<WriteReplyEvent> = _events.asSharedFlow()

    fun sendReply(oid: Long, rpid: Long, parent: Long, message: String) {
        if (_uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSending = true,
                error = null
            )

            try {
                val result = replyRepository.sendReply(oid, rpid, parent, message)

                result.onSuccess { reply ->
                    _uiState.value = _uiState.value.copy(isSending = false)
                    MsgUtil.showMsg("发送成功")
                    _events.emit(WriteReplyEvent.Success)
                }.onFailure { error ->
                    val errorMessage = resolveWriteReplyError(error)

                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = errorMessage
                    )
                    MsgUtil.showMsg("评论发送失败：$errorMessage")
                    _events.emit(WriteReplyEvent.Error(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = "网络错误，请检查网络连接"
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = errorMessage
                )
                MsgUtil.showMsg(errorMessage)
                _events.emit(WriteReplyEvent.Error(errorMessage))
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
