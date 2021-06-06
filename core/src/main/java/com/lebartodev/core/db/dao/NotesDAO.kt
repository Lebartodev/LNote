package com.lebartodev.core.db.dao

import androidx.room.*
import com.lebartodev.core.db.entity.Note
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe


@Dao
interface NotesDAO {
    @Query("SELECT * FROM note WHERE deletedDate is null and created not null ORDER BY created DESC")
    fun getAll(): Flowable<List<Note>>

    @Query("SELECT * FROM note WHERE deletedDate is not null and created not null ORDER BY created DESC")
    fun getArchivedNotes(): Flowable<List<Note>>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Long): Flowable<Note>

    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note WHERE id = :id")
    fun deleteById(id: Long)

    @Query("UPDATE note set deletedDate = :deletedDate WHERE id = :id")
    fun markAsDeleted(id: Long, deletedDate: Long): Completable

    @Query("SELECT id FROM note WHERE deletedDate = (SELECT MAX(deletedDate) FROM note)")
    fun lastDeleted(): Maybe<Long>

    @Query("UPDATE note set deletedDate = NULL WHERE id = :id")
    fun restoreNote(id: Long): Completable

    @Query("UPDATE note SET title = :title, text = :text, date = :date WHERE id = :id")
    fun updateById(id: Long, title: String?, text: String?, date: Long?)
}