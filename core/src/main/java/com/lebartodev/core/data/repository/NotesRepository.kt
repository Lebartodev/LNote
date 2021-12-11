package com.lebartodev.core.data.repository

import com.lebartodev.core.data.Manager
import com.lebartodev.core.db.AppDatabase
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.db.entity.Photo
import com.lebartodev.core.di.utils.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AppScope
class NotesRepository @Inject constructor(
    private val database: AppDatabase
) : Repository.Notes {
    override fun getNotes(): Flow<List<Note>> = database.notesDao().getAll()

    override fun getNote(id: Long): Flow<Note> = database.notesDao().getById(id)

    override suspend fun deleteNote(id: Long) = database.notesDao()
        .markAsDeleted(id, System.currentTimeMillis())

    override suspend fun completleDeleteNote(id: Long) = database.notesDao().deleteById(id)

    override suspend fun restoreNote(id: Long) = database.notesDao().restoreNote(id)

    override suspend fun createNote(
        title: String?, text: String?, date: Long?,
        photos: List<Photo>
    ): Long {
        if (text.isNullOrBlank()) {
            throw NullPointerException()
        } else {
            return database.notesDao()
                .insertNote(Note(null, title, date, System.currentTimeMillis(), text)
                    .apply { this.photos = photos })
        }
    }

    override suspend fun deleteDraftedNote(
        title: String?, text: String?, date: Long?,
        photos: List<Photo>
    ) {
        if (!text.isNullOrBlank()) {
            database.notesDao()
                .insertNote(
                    Note(
                        null, title, date, null, text, System.currentTimeMillis()
                    ).apply { this.photos = photos })
        }
    }

    override suspend fun editNote(id: Long, title: String?, text: String?, date: Long?) {
        if (text.isNullOrBlank()) {
            throw NullPointerException()
        } else {
            val note = database.notesDao().getById(id)
                .first()
            note.text = text
            note.date = date
            note.title = title

            database.notesDao().insertNote(note)
        }
    }

    override suspend fun restoreLastNote(): Note {
        val lastDeletedId = database.notesDao().lastDeleted()
        database.notesDao().restoreNote(lastDeletedId)
        return database.notesDao().getById(lastDeletedId).first()
    }

    override fun getArchive(): Flow<List<Note>> = database.notesDao().getArchivedNotes()
}