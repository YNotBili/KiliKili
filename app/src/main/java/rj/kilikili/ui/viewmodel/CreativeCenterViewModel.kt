package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.repository.CreativeCenterRepository
import com.huanli233.biliwebapi.api.interfaces.ICreativeCenterApi.CreatorScrolls
import com.huanli233.biliwebapi.api.interfaces.ICreativeCenterApi.CreatorStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreativeCenterState(
    val stats: CreatorStats? = null,
    val scrolls: CreatorScrolls? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CreativeCenterViewModel @Inject constructor(
    private val creativeCenterRepository: CreativeCenterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreativeCenterState())
    val uiState: StateFlow<CreativeCenterState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CreativeCenterState()
            creativeCenterRepository.getVideoStat().fold(
                onSuccess = { stats ->
                    creativeCenterRepository.getBeUPTime().fold(
                        onSuccess = { scrolls ->
                            _uiState.value = CreativeCenterState(
                                stats = stats,
                                scrolls = scrolls,
                                isLoading = false
                            )
                        },
                        onFailure = { error ->
                            _uiState.value = CreativeCenterState(
                                stats = stats,
                                isLoading = false,
                                error = error.message
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = CreativeCenterState(
                        isLoading = false,
                        error = error.message
                    )
                }
            )
        }
    }
}