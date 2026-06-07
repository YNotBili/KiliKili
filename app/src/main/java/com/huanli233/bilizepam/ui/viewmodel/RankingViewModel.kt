package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.RankingRepository
import com.huanli233.biliwebapi.bean.video.VideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RankingUiState {
    data object Loading : RankingUiState()
    data class Success(val items: List<VideoInfo>) : RankingUiState()
    data class Error(val message: String) : RankingUiState()
}

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RankingUiState>(RankingUiState.Loading)
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init { loadRanking() }

    fun loadRanking(rid: Int = 0, type: String = "all") {
        viewModelScope.launch {
            _uiState.value = RankingUiState.Loading
            rankingRepository.getRanking(rid = rid, type = type).fold(
                onSuccess = { items ->
                    _uiState.value = RankingUiState.Success(items)
                },
                onFailure = { error ->
                    _uiState.value = RankingUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}