package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.bean.article.ArticleInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.ArticleRepository
import javax.inject.Inject

@HiltViewModel
@Deprecated("Move to opus api")
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val article: ArticleInfo? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(cvid: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getArticle(cvid).fold(
                onSuccess = { _uiState.value = UiState(article = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}