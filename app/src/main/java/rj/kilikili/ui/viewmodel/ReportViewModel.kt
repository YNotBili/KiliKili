package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    sealed class Event { data object Success : Event(); data class Failed(val msg: String) : Event() }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 4)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun reportDanmaku(oid: Long, dmid: Long, reason: String, content: String) {
        viewModelScope.launch {
            repository.reportDanmaku(oid, dmid, reason, content).fold(
                onSuccess = { _events.tryEmit(Event.Success) },
                onFailure = { _events.tryEmit(Event.Failed(it.message ?: "举报失败")) }
            )
        }
    }

    fun reportReply(oid: Long, rpid: Long, reason: Int, content: String) {
        viewModelScope.launch {
            repository.reportReply(oid, rpid, reason, content).fold(
                onSuccess = { _events.tryEmit(Event.Success) },
                onFailure = { _events.tryEmit(Event.Failed(it.message ?: "举报失败")) }
            )
        }
    }
}