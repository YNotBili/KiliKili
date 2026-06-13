package rj.kilikili.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import rj.kilikili.data.account.AccountEntity
import rj.kilikili.data.account.AccountRepository
import rj.kilikili.data.setting.LocalData
import rj.kilikili.utils.extensions.LoadState
import com.huanli233.biliwebapi.api.interfaces.ILoginApi
import com.huanli233.biliwebapi.bean.login.QrCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QrCodeLoginState(
    val code: Int = -1,
    val finished: Boolean = false
)

@HiltViewModel
class QrCodeLoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _qrcodeState = MutableStateFlow<LoadState<String>>(LoadState.Loading())
    val qrcodeState = _qrcodeState.asStateFlow()

    private val _qrCodeLoginState = MutableStateFlow<LoadState<QrCodeLoginState>>(LoadState.Loading())
    val qrCodeLoginState = _qrCodeLoginState.asStateFlow()

    private val _needRefresh = MutableStateFlow(false)
    val needRefresh = _needRefresh.asStateFlow()

    private var pollJob: Job? = null
    private var qrcodeKey: String? = null

    init {
        loadQrcode()
    }

    fun loadQrcode() {
        viewModelScope.launch {
            _qrcodeState.value = LoadState.Loading()
            bilibiliApi.api(ILoginApi::class) {
                requestQrCode()
            }.apiResultNonNull().onSuccess {
                qrcodeKey = it.qrcodeKey
                _needRefresh.value = false
                _qrcodeState.value = LoadState.Success(it.url)
                startPoll()
            }.onFailure {
                _qrcodeState.value = LoadState.Error(it)
                _needRefresh.value = true
            }
        }
    }

    private fun startPoll() {
        stopPoll()
        pollJob = viewModelScope.launch {
            while (isActive) {
                bilibiliApi.api(ILoginApi::class) {
                    qrCodeLogin(qrcodeKey.orEmpty())
                }.apiResultNonNull().onSuccess {
                    _qrCodeLoginState.value = LoadState.Success(
                        _root_ide_package_.rj.kilikili.ui.screens.login.QrCodeLoginState(
                            it.code
                        )
                    )
                    if (it.code == 0) {
                        stopPoll()
                        login(it)
                    } else if (it.code != 86090 && it.code != 86101) {
                        _needRefresh.value = true
                        stopPoll()
                    }
                }.onFailure {
                    _needRefresh.value = true
                    _qrCodeLoginState.value = LoadState.Error(it)
                }
                delay(1000)
            }
        }
    }

    private fun stopPoll() {
        pollJob?.cancel()
    }

    private fun login(loginResult: QrCode.LoginResult) {
        viewModelScope.launch {
            accountRepository.addAccount(
                AccountEntity(
                    accountId = LocalData.settings.activeAccountId,
                    refreshToken = loginResult.refreshToken,
                    lastActiveTime = System.currentTimeMillis()
                )
            )
            _qrCodeLoginState.value = LoadState.Success(
                _root_ide_package_.rj.kilikili.ui.screens.login.QrCodeLoginState(
                    0,
                    true
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPoll() // 确保ViewModel销毁时停止轮询
    }
}