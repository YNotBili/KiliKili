package com.huanli233.bilizepam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.repository.TimelineRepository
import com.huanli233.biliwebapi.bean.timeline.TimelineDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TimelineUiState {
    data object Loading : TimelineUiState()
    data class Success(val days: List<TimelineDay>) : TimelineUiState()
    data class Error(val message: String) : TimelineUiState()
}

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val timelineRepository: TimelineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init { loadTimeline() }

    fun loadTimeline() {
        viewModelScope.launch {
            _uiState.value = TimelineUiState.Loading
            timelineRepository.getTimeline().fold(
                onSuccess = { days ->
                    _uiState.value = TimelineUiState.Success(days)
                },
                onFailure = { error ->
                    _uiState.value = TimelineUiState.Error(error.message ?: "未知错误")
                }
            )
        }
    }
}