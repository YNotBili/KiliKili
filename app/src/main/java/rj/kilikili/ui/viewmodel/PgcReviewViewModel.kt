package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IPgcReviewApi.PgcReviewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.PgcReviewRepository
import javax.inject.Inject

@HiltViewModel
class PgcReviewViewModel @Inject constructor(
    private val repository: PgcReviewRepository
) : ViewModel() {

    sealed class Tab { data object Short : Tab(); data object Long : Tab() }

    data class UiState(
        val isLoading: Boolean = false,
        val tab: Tab = Tab.Short,
        val mediaId: Long = 0,
        val items: List<PgcReviewItem> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setTab(tab: Tab) { _uiState.value = _uiState.value.copy(tab = tab) }

    fun load(mediaId: Long, sort: Int = 0) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(mediaId = mediaId, isLoading = true, error = null)
        viewModelScope.launch {
            val result = if (_uiState.value.tab == Tab.Short) repository.getShortReviews(mediaId, sort)
                         else repository.getLongReviews(mediaId)
            result.fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, items = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun postShort(mediaId: Long, content: String, score: Int, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.postShortReview(mediaId, content, score).fold(
                onSuccess = { onDone(true, null); load(mediaId) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun like(reviewId: Long, action: Int = 1, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.likeReview(reviewId, action).fold(
                onSuccess = { onDone(true, null) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}