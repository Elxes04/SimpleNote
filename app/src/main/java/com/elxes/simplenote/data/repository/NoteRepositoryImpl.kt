package com.elxes.simplenote.data.repository

import com.elxes.simplenote.data.local.dao.NoteDao
import com.elxes.simplenote.data.mapper.toNote
import com.elxes.simplenote.data.mapper.toNoteEntity
import com.elxes.simplenote.domain.model.Note
import com.elxes.simplenote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> {
        return dao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun getNoteById(id: Long): Note? {
        return dao.getNoteById(id)?.toNote()
    }

    override suspend fun insertNote(note: Note): Long {
        return dao.insertNote(note.toNoteEntity())
    }

    override suspend fun updateNote(note: Note) {
        dao.updateNote(note.toNoteEntity())
    }

    override suspend fun deleteNote(note: Note) {
        dao.deleteNote(note.toNoteEntity())
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return dao.searchNotes(query).map { entities ->
            entities.map { it.toNote() }
        }
    }

    override suspend fun deleteAllNotes() {
        dao.deleteAllNotes()
    }
}
