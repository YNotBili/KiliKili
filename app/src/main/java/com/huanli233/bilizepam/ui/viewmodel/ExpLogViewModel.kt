package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.MemberRepository
import com.huanli233.biliwebapi.bean.member.ExpLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExpLogUiState {
    data object Loading : ExpLogUiState()
    data class Success(val logs: List<ExpLog>) : ExpLogUiState()
    data class Error(val message: String) : ExpLogUiState()
}

@HiltViewModel
class ExpLogViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExpLogUiState>(ExpLogUiState.Loading)
    val uiState: StateFlow<ExpLogUiState> = _uiState.asStateFlow()

    init { loadExpLog() }

    fun loadExpLog() {
        viewModelScope.launch {
            _uiState.value = ExpLogUiState.Loading
            memberRepository.getExpLog().fold(
                onSuccess = { logs ->
                    _uiState.value = ExpLogUiState.Success(logs)
                },
                onFailure = { error ->
                    _uiState.value = ExpLogUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}