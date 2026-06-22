package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.bean.login.TvCookie
import com.huanli233.biliwebapi.bean.login.TvQrCodePoll
import com.huanli233.biliwebapi.bean.login.TvTokenInfo
import rj.kilikili.data.account.AccountEntity
import rj.kilikili.data.account.AccountRepository
import rj.kilikili.data.account.CookieEntity
import rj.kilikili.data.repository.HdLoginRepository
import rj.kilikili.data.setting.LocalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class HdQrCodeState(
    val qrCodeUrl: String? = null,
    val status: HdQrStatus = HdQrStatus.REQUESTING,
    val error: String? = null
)

enum class HdQrStatus {
    REQUESTING, WAITING, SCANNED, LOGIN_SUCCESS, EXPIRED, ERROR
}

@HiltViewModel
class HdQrCodeLoginViewModel @Inject constructor(
    private val hdLoginRepository: HdLoginRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HdQrCodeState())
    val uiState: StateFlow<HdQrCodeState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    init { requestQrCode() }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
    }

    fun requestQrCode() {
        pollJob?.cancel()
        _uiState.value = HdQrCodeState(status = HdQrStatus.REQUESTING)

        viewModelScope.launch {
            hdLoginRepository.getAuthCode()
                .onSuccess { data ->
                    _uiState.value = HdQrCodeState(
                        qrCodeUrl = data.url,
                        status = HdQrStatus.WAITING
                    )
                    startPolling(data.authCode)
                }
                .onFailure { e ->
                    _uiState.value = HdQrCodeState(
                        status = HdQrStatus.ERROR,
                        error = e.message ?: "获取二维码失败"
                    )
                }
        }
    }

    private fun startPolling(authCode: String) {
        pollJob = viewModelScope.launch {
            for (i in 0 until 180) {
                delay(1000.milliseconds)
                if (!isActive) break

                hdLoginRepository.pollQrCode(authCode)
                    .onSuccess { poll ->
                        if (poll.tokenInfo != null) {
                            saveLoginResult(poll)
                            return@launch
                        }
                    }
                    .onFailure { e ->
                        val msg = e.message ?: ""
                        when {
                            msg.contains("86038") -> {
                                _uiState.value = HdQrCodeState(status = HdQrStatus.EXPIRED)
                                return@launch
                            }
                            // 86039/86042: 尚未扫码；86090: 已扫码未确认 → 继续轮询
                            msg.contains("86039") || msg.contains("86042") -> { }
                            msg.contains("86090") -> {
                                _uiState.value = HdQrCodeState(status = HdQrStatus.SCANNED)
                            }
                            else -> {
                                _uiState.value = HdQrCodeState(status = HdQrStatus.ERROR, error = msg)
                                return@launch
                            }
                        }
                    }
            }
            // 轮询结束（180s）未扫码成功 → 过期
            _uiState.value = HdQrCodeState(status = HdQrStatus.EXPIRED)
        }
    }

    /**
     * 保存 HD 登录结果：
     * 1. 创建 AccountEntity（mid + refreshToken）
     * 2. 保存 Set-Cookie 中的 cookies
     * 3. 切换为当前账号
     * 4. 标记 isHd
     */
    private suspend fun saveLoginResult(poll: TvQrCodePoll) {
        val tokenInfo = poll.tokenInfo ?: return
        val mid = tokenInfo.mid
        val cookies = poll.cookieInfo?.cookies.orEmpty()

        // 创建账号
        val account = AccountEntity(
            accountId = mid,
            refreshToken = tokenInfo.refreshToken,
            appKey = "android_hd",
            lastActiveTime = System.currentTimeMillis()
        )

        // 保存 cookies
        val cookieEntities = cookies.map { it.toCookieEntity(mid) }

        // 写入数据库 + 切换到该账号
        accountRepository.addAccount(account)
        accountRepository.addCookies(cookieEntities)
        accountRepository.setActiveAccount(mid)

        // 标记 HD 模式
        LocalData.edit { isHd = 1 }

        _uiState.value = HdQrCodeState(status = HdQrStatus.LOGIN_SUCCESS)
    }
}

/** 将 HD 登录返回的 TvCookie 转为持久化的 CookieEntity */
private fun TvCookie.toCookieEntity(accountId: Long) = CookieEntity(
    accountId = accountId,
    name = name,
    value = value,
    expires = if (expires > 0) expires else null,
    domain = ".bilibili.com",
    path = "/",
    secure = false,
    httpOnly = httpOnly == 1,
)
