package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.DanmakuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DanmakuSendState(
    val text: String = "",
    val color: Int = 0xFFFFFF,
    val mode: Int = 1,
    val isSending: Boolean = false,
    val result: String? = null
)

@HiltViewModel
class DanmakuSendViewModel @Inject constructor(
    private val danmakuRepository: DanmakuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DanmakuSendState())
    val uiState: StateFlow<DanmakuSendState> = _uiState.asStateFlow()

    fun updateText(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }

    fun updateColor(color: Int) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    fun updateMode(mode: Int) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }

    fun send(oid: Long, aid: Long, bvid: String, progress: Long) {
        val state = _uiState.value
        if (state.text.isBlank() || state.isSending) return

        _uiState.value = state.copy(isSending = true, result = null)

        viewModelScope.launch {
            danmakuRepository.sendDanmaku(
                oid = oid,
                message = state.text,
                progress = progress,
                aid = aid,
                bvid = bvid,
                color = state.color,
                mode = state.mode
            ).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        result = "发送成功喵~"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        result = "发送失败：${error.message ?: "未知错误"}"
                    )
                }
            )
        }
    }
}