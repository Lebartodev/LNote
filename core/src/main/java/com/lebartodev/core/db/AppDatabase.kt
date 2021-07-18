package com.lebartodev.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lebartodev.core.db.dao.NotesDAO
import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.db.entity.NoteEntity
import com.lebartodev.core.db.entity.Photo
import com.lebartodev.core.di.utils.AppScope

@AppScope
@Database(entities = [NoteEntity::class, Photo::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDAO
}