package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.MemberRepository
import com.huanli233.biliwebapi.bean.member.CoinLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CoinLogUiState {
    data object Loading : CoinLogUiState()
    data class Success(val logs: List<CoinLog>) : CoinLogUiState()
    data class Error(val message: String) : CoinLogUiState()
}

@HiltViewModel
class CoinLogViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CoinLogUiState>(CoinLogUiState.Loading)
    val uiState: StateFlow<CoinLogUiState> = _uiState.asStateFlow()

    init { loadCoinLog() }

    fun loadCoinLog() {
        viewModelScope.launch {
            _uiState.value = CoinLogUiState.Loading
            memberRepository.getCoinLog().fold(
                onSuccess = { logs ->
                    _uiState.value = CoinLogUiState.Success(logs)
                },
                onFailure = { error ->
                    _uiState.value = CoinLogUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}