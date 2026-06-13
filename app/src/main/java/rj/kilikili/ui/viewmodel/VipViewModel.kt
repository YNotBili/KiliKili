package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.VipRepository
import com.huanli233.biliwebapi.bean.vip.VipInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VipUiState {
    data object Loading : VipUiState()
    data class Success(val info: VipInfo) : VipUiState()
    data class Error(val message: String) : VipUiState()
}

@HiltViewModel
class VipViewModel @Inject constructor(
    private val vipRepository: VipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VipUiState>(VipUiState.Loading)
    val uiState: StateFlow<VipUiState> = _uiState.asStateFlow()

    private var _checkInResult = MutableStateFlow<String?>(null)
    val checkInResult: StateFlow<String?> = _checkInResult.asStateFlow()

    init { loadVipInfo() }

    fun loadVipInfo() {
        viewModelScope.launch {
            _uiState.value = VipUiState.Loading
            vipRepository.getVipInfo().fold(
                onSuccess = { info ->
                    _uiState.value = VipUiState.Success(info)
                },
                onFailure = { error ->
                    _uiState.value = VipUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }

    fun checkIn() {
        viewModelScope.launch {
            vipRepository.addExperience().fold(
                onSuccess = {
                    _checkInResult.value = "签到成功"
                },
                onFailure = { error ->
                    _checkInResult.value = error.message ?: "签到失败"
                }
            )
        }
    }
}