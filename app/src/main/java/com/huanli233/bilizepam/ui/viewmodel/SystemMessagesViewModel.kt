package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.MessageRepository
import com.huanli233.biliwebapi.bean.message.SystemMessageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SystemMessagesUiState {
    data object Loading : SystemMessagesUiState()
    data class Success(val items: List<SystemMessageItem>) : SystemMessagesUiState()
    data class Error(val message: String) : SystemMessagesUiState()
}

@HiltViewModel
class SystemMessagesViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SystemMessagesUiState>(SystemMessagesUiState.Loading)
    val uiState: StateFlow<SystemMessagesUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = SystemMessagesUiState.Loading
            messageRepository.getSystemMessages()
                .onSuccess { result ->
                    _uiState.value = SystemMessagesUiState.Success(result.systemNotifyList)
                }.onFailure { error ->
                    _uiState.value = SystemMessagesUiState.Error(error.message ?: "未知错误")
                }
        }
    }
}