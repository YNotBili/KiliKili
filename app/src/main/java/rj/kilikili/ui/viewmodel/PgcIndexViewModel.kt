package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.IPgcExApi
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.PgcExRepository
import javax.inject.Inject

@HiltViewModel
class PgcIndexViewModel @Inject constructor(
    private val repository: PgcExRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val filters: List<IPgcExApi.PgcFilterGroup> = emptyList(),
        val selectedArea: String = "-1",
        val selectedSeasonType: Int = 1,
        val list: List<BangumiDetail> = emptyList(),
        val page: Int = 1,
        val hasMore: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadCondition() {
        viewModelScope.launch {
            repository.getIndexCondition().onSuccess {
                _uiState.value = _uiState.value.copy(filters = it.filter)
            }.onFailure {
                _uiState.value = _uiState.value.copy(error = it.message)
            }
        }
    }

    fun setArea(area: String) {
        _uiState.value = _uiState.value.copy(selectedArea = area, list = emptyList(), page = 1, hasMore = false)
        fetch(reset = true)
    }

    fun setSeasonType(type: Int) {
        _uiState.value = _uiState.value.copy(selectedSeasonType = type, list = emptyList(), page = 1, hasMore = false)
        fetch(reset = true)
    }

    fun refresh() = fetch(reset = true)

    fun loadMore() {
        val s = _uiState.value
        if (!s.hasMore || s.isLoading) return
        fetch(reset = false)
    }

    fun toggleFollow(seasonId: Long, followed: Boolean, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = if (followed) repository.unfollowPgc(seasonId) else repository.followPgc(seasonId)
            result.fold(onSuccess = { onDone(true, null) }, onFailure = { onDone(false, it.message) })
        }
    }

    private fun fetch(reset: Boolean) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = if (reset) null else _uiState.value.error)
        val nextPage = if (reset) 1 else _uiState.value.page + 1
        viewModelScope.launch {
            repository.getIndexResult(
                area = _uiState.value.selectedArea,
                seasonType = _uiState.value.selectedSeasonType,
                page = nextPage,
                pageSize = 20
            ).fold(
                onSuccess = { list ->
                    val accumulated = if (reset) list else _uiState.value.list + list
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        list = accumulated,
                        page = nextPage,
                        hasMore = list.size >= 20
                    )
                },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}

@HiltViewModel
class PgcRankViewModel @Inject constructor(
    private val repository: PgcExRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val list: List<BangumiDetail> = emptyList(),
        val seasonType: Int = 1,
        val day: Int = 3,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setSeasonType(type: Int) { _uiState.value = _uiState.value.copy(seasonType = type); refresh() }
    fun setDay(day: Int) { _uiState.value = _uiState.value.copy(day = day); refresh() }

    fun refresh() {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getPgcRank(_uiState.value.seasonType, _uiState.value.day).fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false, list = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }
}