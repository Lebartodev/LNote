package com.lebartodev.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lebartodev.core.db.dao.NotesDAO
import com.lebartodev.core.db.entity.Note


@Database(entities = [Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDAO
}