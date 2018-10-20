package com.lebartodev.lnote.data.dao

import androidx.room.*
import com.lebartodev.lnote.data.entity.Note


@Dao
interface NotesDAO {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getById(id: Long): Note

    @Insert
    fun insert(note: Note): Long

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)
}