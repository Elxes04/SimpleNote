package com.elxes.simplenote.presentation.note_editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elxes.simplenote.domain.model.Note
import com.elxes.simplenote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val repository: NoteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var noteTitle by mutableStateOf("")
        private set

    var noteContent by mutableStateOf("")
        private set

    var isPreviewMode by mutableStateOf(false)
        private set

    private var currentNoteId: Long? = null

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        savedStateHandle.get<Long>("noteId")?.let { noteId ->
            if (noteId != -1L) {
                viewModelScope.launch {
                    repository.getNoteById(noteId)?.let { note ->
                        currentNoteId = note.id
                        noteTitle = note.title
                        noteContent = note.content
                    }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        noteTitle = title
    }

    fun onContentChange(content: String) {
        noteContent = content
    }

    fun togglePreviewMode() {
        isPreviewMode = !isPreviewMode
    }

    fun saveNote() {
        viewModelScope.launch {
            if (noteTitle.isBlank() && noteContent.isBlank()) {
                _eventFlow.send(UiEvent.ShowSnackbar("Note is empty"))
                return@launch
            }

            val timestamp = System.currentTimeMillis()
            val note = Note(
                id = currentNoteId ?: 0,
                title = noteTitle,
                content = noteContent,
                createdAt = timestamp,
                updatedAt = timestamp
            )

            if (currentNoteId == null) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }
            _eventFlow.send(UiEvent.SaveNote)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}
