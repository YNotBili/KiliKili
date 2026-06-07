package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.DynamicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SendDynamicState(
    val text: String = "",
    val isSending: Boolean = false,
    val result: Long? = null,
    val error: String? = null
)

@HiltViewModel
class SendDynamicViewModel @Inject constructor(
    private val dynamicRepository: DynamicRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SendDynamicState())
    val state: StateFlow<SendDynamicState> = _state.asStateFlow()

    fun updateText(text: String) {
        _state.value = _state.value.copy(text = text)
    }

    fun publish() {
        val currentText = _state.value.text
        if (currentText.isBlank()) return

        _state.value = _state.value.copy(isSending = true, error = null, result = null)

        viewModelScope.launch {
            dynamicRepository.publishTextDynamic(currentText).fold(
                onSuccess = { dynamicId ->
                    _state.value = _state.value.copy(
                        isSending = false,
                        result = dynamicId
                    )
                },
                onFailure = { throwable ->
                    _state.value = _state.value.copy(
                        isSending = false,
                        error = throwable.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun reset() {
        _state.value = SendDynamicState()
    }
}