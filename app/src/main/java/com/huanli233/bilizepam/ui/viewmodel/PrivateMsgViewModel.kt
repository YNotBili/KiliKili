package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.PrivateMsgRepository
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMsgSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PrivateMsgUiState {
    data object Loading : PrivateMsgUiState()
    data class Success(val sessions: List<PrivateMsgSession>) : PrivateMsgUiState()
    data class Error(val message: String) : PrivateMsgUiState()
}

@HiltViewModel
class PrivateMsgViewModel @Inject constructor(
    private val privateMsgRepository: PrivateMsgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PrivateMsgUiState>(PrivateMsgUiState.Loading)
    val uiState: StateFlow<PrivateMsgUiState> = _uiState.asStateFlow()

    init { loadSessions() }

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = PrivateMsgUiState.Loading
            privateMsgRepository.getSessions().fold(
                onSuccess = { result ->
                    _uiState.value = PrivateMsgUiState.Success(result.sessionList)
                },
                onFailure = { error ->
                    _uiState.value = PrivateMsgUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}