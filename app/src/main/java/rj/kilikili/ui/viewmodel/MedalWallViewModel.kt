package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IUserApi
import com.huanli233.biliwebapi.bean.user.FansMedalInfo
import rj.kilikili.data.account.AccountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MedalWallUiState {
    data object Loading : MedalWallUiState()
    data class Success(val medals: List<FansMedalInfo>) : MedalWallUiState()
    data class Error(val message: String) : MedalWallUiState()
}

@HiltViewModel
class MedalWallViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MedalWallUiState>(MedalWallUiState.Loading)
    val uiState: StateFlow<MedalWallUiState> = _uiState.asStateFlow()

    init { loadMedals() }

    fun loadMedals() {
        viewModelScope.launch {
            _uiState.value = MedalWallUiState.Loading
            val mid = AccountManager.currentAccount.accountId
            bilibiliApi.api(IUserApi::class) {
                getCard(mid.toString())
            }.apiResultNonNull().fold(
                onSuccess = { cardInfo ->
                    val userInfo = cardInfo.toUserInfo()
                    val medal = userInfo.fansMedal
                    _uiState.value = MedalWallUiState.Success(
                        if (medal != null) listOf(medal) else emptyList()
                    )
                },
                onFailure = { error ->
                    _uiState.value = MedalWallUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}