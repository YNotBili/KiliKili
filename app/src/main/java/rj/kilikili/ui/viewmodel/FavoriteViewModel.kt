package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.repository.FavoriteExRepository
import rj.kilikili.data.repository.FavoriteRepository
import com.huanli233.biliwebapi.bean.favorite.FavoriteBox
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FavoriteUiState {
    data object Loading : FavoriteUiState()
    data class Success(val folders: List<FavoriteBox>) : FavoriteUiState()
    data class Error(val message: String) : FavoriteUiState()
}

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val favoriteExRepository: FavoriteExRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteFolders()
    }

    fun loadFavoriteFolders() {
        viewModelScope.launch {
            _uiState.value = FavoriteUiState.Loading

            val mid = AccountManager.currentAccount.accountId
            favoriteRepository.getFavoriteBoxList(mid).fold(
                onSuccess = { response ->
                    _uiState.value = FavoriteUiState.Success(response.list ?: emptyList())
                },
                onFailure = { error ->
                    _uiState.value = FavoriteUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
            )
        }
    }

    fun createFolder(title: String, intro: String = "", onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            favoriteExRepository.addFolder(title, intro).fold(
                onSuccess = { onDone(true, null); loadFavoriteFolders() },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun renameFolder(mediaId: Long, title: String, intro: String = "", onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            favoriteExRepository.editFolder(mediaId, title, intro).fold(
                onSuccess = { onDone(true, null); loadFavoriteFolders() },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun deleteFolder(mediaId: Long, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            favoriteExRepository.deleteFolder(mediaId).fold(
                onSuccess = { onDone(true, null); loadFavoriteFolders() },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}
