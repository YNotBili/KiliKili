package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.HdLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HdQrCodeState(
    val qrCodeUrl: String? = null,
    val status: HdQrStatus = HdQrStatus.REQUESTING,
    val error: String? = null
)

enum class HdQrStatus {
    REQUESTING, WAITING, LOGIN_SUCCESS, EXPIRED, ERROR
}

@HiltViewModel
class HdQrCodeLoginViewModel @Inject constructor(
    private val hdLoginRepository: HdLoginRepository
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
                delay(1000)
                if (!isActive) break

                hdLoginRepository.pollQrCode(authCode)
                    .onSuccess { poll ->
                        if (poll.status && poll.tokenInfo != null) {
                            rj.kilikili.data.setting.LocalData.edit { isHd = 1 }
                            _uiState.value = HdQrCodeState(status = HdQrStatus.LOGIN_SUCCESS)
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
                            else -> {
                                _uiState.value = HdQrCodeState(status = HdQrStatus.ERROR, error = msg)
                                return@launch
                            }
                        }
                    }
            }
        }
    }
}