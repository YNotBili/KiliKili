package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.repository.MemberRepository
import com.huanli233.biliwebapi.bean.member.LoginRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginRecordUiState {
    data object Loading : LoginRecordUiState()
    data class Success(val record: LoginRecord) : LoginRecordUiState()
    data class Error(val message: String) : LoginRecordUiState()
}

@HiltViewModel
class LoginRecordViewModel @Inject constructor(
    private val memberRepository: MemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginRecordUiState>(LoginRecordUiState.Loading)
    val uiState: StateFlow<LoginRecordUiState> = _uiState.asStateFlow()

    init { loadLoginRecord() }

    fun loadLoginRecord(buvid: String = "") {
        viewModelScope.launch {
            _uiState.value = LoginRecordUiState.Loading
            val mid = AccountManager.currentAccount.accountId
            memberRepository.getLoginRecord(mid, buvid).fold(
                onSuccess = { record ->
                    _uiState.value = LoginRecordUiState.Success(record)
                },
                onFailure = { error ->
                    _uiState.value = LoginRecordUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}