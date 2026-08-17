package com.elxes.simplenote.presentation.note_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elxes.simplenote.domain.model.Note
import com.elxes.simplenote.domain.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedNoteIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedNoteIds = _selectedNoteIds.asStateFlow()

    @OptIn(FlowPreview::class)
    val notes = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleNoteSelection(noteId: Long) {
        _selectedNoteIds.update { current ->
            if (current.contains(noteId)) {
                current - noteId
            } else {
                current + noteId
            }
        }
    }

    fun clearSelection() {
        _selectedNoteIds.value = emptySet()
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            val idsToDelete = _selectedNoteIds.value
            notes.value.filter { it.id in idsToDelete }.forEach { note ->
                repository.deleteNote(note)
            }
            _selectedNoteIds.value = emptySet()
        }
    }

    fun getSelectedNotesText(): String {
        val selectedIds = _selectedNoteIds.value
        return notes.value
            .filter { it.id in selectedIds }
            .joinToString("\n\n---\n\n") { note ->
                val title = if (note.title.isNotBlank()) "${note.title}\n\n" else ""
                title + note.content
            }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
