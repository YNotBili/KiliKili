package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class QualityOption(
    val qn: Int,
    val label: String
)

data class QualityChooserState(
    val availableQualities: List<QualityOption> = listOf(
        QualityOption(10000, "4K 超高清"),
        QualityOption(400, "1080P 高码率"),
        QualityOption(250, "1080P 高清"),
        QualityOption(120, "720P 高清"),
        QualityOption(80, "480P 清晰"),
        QualityOption(32, "360P 流畅")
    ),
    val selectedQn: Int = 80,
    val confirmResult: Int? = null
)

@HiltViewModel
class QualityChooserViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QualityChooserState())
    val uiState: StateFlow<QualityChooserState> = _uiState.asStateFlow()

    fun selectQuality(qn: Int) {
        _uiState.value = _uiState.value.copy(selectedQn = qn)
    }

    fun confirm() {
        _uiState.value = _uiState.value.copy(confirmResult = _uiState.value.selectedQn)
    }
}