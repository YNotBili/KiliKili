package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.bean.series.SeriesInfo
import com.huanli233.biliwebapi.bean.video.VideoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.SeriesRepository
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val repository: SeriesRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val meta: SeriesInfo.Meta? = null,
        val videos: List<VideoInfo> = emptyList(),
        val page: Int = 1,
        val total: Int = 0,
        val hasMore: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var mid: Long = 0
    private var seasonId: Long = 0
    private var loading = false

    fun load(mid: Long, seasonId: Long) {
        if (this.mid == mid && this.seasonId == seasonId && _uiState.value.videos.isNotEmpty()) return
        this.mid = mid
        this.seasonId = seasonId
        fetch(reset = true)
    }

    fun refresh() {
        fetch(reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || loading) return
        fetch(reset = false)
    }

    private fun fetch(reset: Boolean) {
        if (loading) return
        loading = true
        val nextPage = if (reset) 1 else _uiState.value.page + 1
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = if (reset) null else _uiState.value.error
        )
        viewModelScope.launch {
            val result = repository.getSeasonVideos(mid, seasonId, page = nextPage, pageSize = 30)
            result.fold(
                onSuccess = { info ->
                    val accumulated = if (reset) info.archives else _uiState.value.videos + info.archives
                    val pageSize = info.page?.pageSize ?: 30
                    val total = info.page?.total ?: info.archives.size
                    val hasMore = info.archives.size >= pageSize && accumulated.size < total
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        meta = if (reset) info.meta else _uiState.value.meta,
                        videos = accumulated,
                        page = nextPage,
                        total = total,
                        hasMore = hasMore,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "加载失败"
                    )
                }
            )
            loading = false
        }
    }
}