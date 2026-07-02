package rj.kilikili.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.biliwebapi.api.interfaces.INoteApi.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rj.kilikili.data.repository.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val notes: List<NoteItem> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadForArchive(oid: Long) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repository.getArchiveNotes(oid).fold(
                onSuccess = { _uiState.value = UiState(notes = it) },
                onFailure = { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
            )
        }
    }

    fun add(oid: Long, title: String, content: String, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.addNote(oid, title, content).fold(
                onSuccess = { onDone(true, null); loadForArchive(oid) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }

    fun delete(noteId: Long, oid: Long, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            repository.deleteNote(noteId).fold(
                onSuccess = { onDone(true, null); loadForArchive(oid) },
                onFailure = { onDone(false, it.message) }
            )
        }
    }
}