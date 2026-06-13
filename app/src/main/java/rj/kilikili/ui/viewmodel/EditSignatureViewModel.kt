package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IUserApi
import rj.kilikili.data.account.AccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditSignatureState(
    val currentSign: String = "",
    val newSign: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val result: String? = null
)

@HiltViewModel
class EditSignatureViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditSignatureState())
    val uiState: StateFlow<EditSignatureState> = _uiState.asStateFlow()

    init { loadCurrentSign() }

    fun loadCurrentSign() {
        viewModelScope.launch {
            _uiState.value = EditSignatureState(isLoading = true)
            val mid = AccountManager.currentAccount.accountId
            bilibiliApi.api(IUserApi::class) {
                getCard(mid.toString())
            }.apiResultNonNull().fold(
                onSuccess = { cardInfo ->
                    val sign = cardInfo.card.sign
                    _uiState.value = EditSignatureState(
                        currentSign = sign,
                        newSign = sign,
                        isLoading = false
                    )
                },
                onFailure = {
                    _uiState.value = EditSignatureState(isLoading = false)
                }
            )
        }
    }

    fun updateSign(text: String) {
        _uiState.value = _uiState.value.copy(newSign = text)
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, result = "保存成功（演示）")
        }
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(result = null)
    }
}