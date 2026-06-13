package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MessageSettingsUiState(
    val msgNotify: Boolean = true,
    val showUnfollowed: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class MessageSettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MessageSettingsUiState())
    val uiState: StateFlow<MessageSettingsUiState> = _uiState.asStateFlow()

    fun setMsgNotify(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(msgNotify = enabled)
    }

    fun setShowUnfollowed(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(showUnfollowed = enabled)
    }
}