package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import rj.kilikili.data.paging.DynamicPagingSource
import rj.kilikili.data.paging.UserSpaceDynamicPagingSource
import rj.kilikili.data.repository.DynamicExRepository
import rj.kilikili.data.repository.DynamicRepository
import com.huanli233.biliwebapi.bean.dynamic.Dynamic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val repository: DynamicRepository,
    private val exRepository: DynamicExRepository
) : ViewModel() {

    sealed class DynamicEvent {
        data object Removed : DynamicEvent()
        data object Topped : DynamicEvent()
        data object Untopped : DynamicEvent()
        data object Edited : DynamicEvent()
        data class Failed(val msg: String) : DynamicEvent()
    }

    private val _events = MutableSharedFlow<DynamicEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<DynamicEvent> = _events.asSharedFlow()
    
    private val likeStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val hostUidState = MutableStateFlow<Long?>(null)
    private val refreshTrigger = MutableStateFlow(0)
    
    private val baseDynamicFlow: Flow<PagingData<Dynamic>> = combine(
        hostUidState,
        refreshTrigger
    ) { hostUid, _ ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                if (hostUid != null) {
                    UserSpaceDynamicPagingSource(hostUid, repository)
                } else {
                    DynamicPagingSource(repository)
                }
            }
        ).flow
    }.flatMapLatest { it }.cachedIn(viewModelScope)
    
    val dynamicFlow: Flow<PagingData<Dynamic>> = baseDynamicFlow.combine(likeStates) { pagingData, likes ->
        pagingData.map { dynamic ->
            val likeState = likes[dynamic.id]
            if (likeState != null) {
                val updatedStats = dynamic.modules.statsModule.copy(
                    like = dynamic.modules.statsModule.like.copy(
                        status = likeState
                    )
                )
                val updatedModules = dynamic.modules.copy(statsModule = updatedStats)
                dynamic.copy(modules = updatedModules)
            } else {
                dynamic
            }
        }
    }
    
    fun likeDynamic(dynamicId: String, isLiked: Boolean) {
        val newState = !isLiked
        likeStates.value = likeStates.value + (dynamicId to newState)
        
        viewModelScope.launch {
            repository.likeDynamic(dynamicId, if (isLiked) 0 else 1)
        }
    }
    
    fun setHostUid(hostUid: Long?) {
        hostUidState.value = hostUid
    }

    fun remove(dynamicId: String) {
        viewModelScope.launch {
            exRepository.removeDynamic(dynamicId.toLongOrNull() ?: return@launch).fold(
                onSuccess = { refreshTrigger.value++; _events.tryEmit(DynamicEvent.Removed) },
                onFailure = { _events.tryEmit(DynamicEvent.Failed(it.message ?: "删除失败")) }
            )
        }
    }

    fun setTop(dynamicId: String) {
        viewModelScope.launch {
            exRepository.setTop(dynamicId.toLongOrNull() ?: return@launch).fold(
                onSuccess = { _events.tryEmit(DynamicEvent.Topped) },
                onFailure = { _events.tryEmit(DynamicEvent.Failed(it.message ?: "置顶失败")) }
            )
        }
    }

    fun removeTop(dynamicId: String) {
        viewModelScope.launch {
            exRepository.removeTop(dynamicId.toLongOrNull() ?: return@launch).fold(
                onSuccess = { _events.tryEmit(DynamicEvent.Untopped) },
                onFailure = { _events.tryEmit(DynamicEvent.Failed(it.message ?: "取消置顶失败")) }
            )
        }
    }

    fun edit(dynamicId: String, content: String) {
        viewModelScope.launch {
            exRepository.editDynamic(dynamicId.toLongOrNull() ?: return@launch, content).fold(
                onSuccess = { refreshTrigger.value++; _events.tryEmit(DynamicEvent.Edited) },
                onFailure = { _events.tryEmit(DynamicEvent.Failed(it.message ?: "编辑失败")) }
            )
        }
    }
}
