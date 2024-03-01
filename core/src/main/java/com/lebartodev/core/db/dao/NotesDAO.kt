package com.lebartodev.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.db.entity.NoteEntity
import com.lebartodev.core.db.entity.Photo
import kotlinx.coroutines.flow.Flow

@Suppress("Detekt.TooManyFunctions")
@Dao
interface NotesDAO {
    @Transaction
    @Query("SELECT * FROM noteentity WHERE deletedDate is null and created not null ORDER BY created DESC")
    fun getAll(): Flow<List<Note>>

    @Transaction
    @Query("SELECT * FROM noteentity WHERE deletedDate is not null and created not null ORDER BY created DESC")
    fun getArchivedNotes(): Flow<List<Note>>

    @Transaction
    @Query("SELECT * FROM noteentity WHERE id = :id")
    fun getById(id: Long): Flow<Note>

    @Insert
    suspend fun insertNoteEntity(note: NoteEntity): Long

    @Update
    suspend fun updateNoteEntity(note: NoteEntity): Int

    @Query("DELETE FROM noteentity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE noteentity set deletedDate = :deletedDate WHERE id = :id")
    suspend fun markAsDeleted(id: Long, deletedDate: Long)

    @Query("SELECT id FROM noteentity WHERE deletedDate = (SELECT MAX(deletedDate) FROM noteentity)")
    suspend fun lastDeleted(): Long

    @Query("UPDATE noteentity set deletedDate = NULL WHERE id = :id")
    suspend fun restoreNote(id: Long)

    @Query("UPDATE noteentity SET title = :title, text = :text, date = :date WHERE id = :id")
    fun updateById(id: Long, title: String?, text: String?, date: Long?)

    @Insert
    suspend fun insertPhoto(photo: Photo): Long

    @Update
    suspend fun updatePhoto(photo: Photo): Int

    @Transaction
    suspend fun insertNote(note: Note): Long {
        val id: Long
        if (updateNoteEntity(note) == 0) {
            id = insertNoteEntity(note)
        } else {
            id = note.id ?: 0L
        }

        val photos = note.photos
        for (photo in photos) {
            photo.noteId = id
            if (updatePhoto(photo) == 0) {
                insertPhoto(photo)
            }
        }
        return id
    }
}