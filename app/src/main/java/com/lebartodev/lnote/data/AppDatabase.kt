package com.lebartodev.lnote.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lebartodev.lnote.data.dao.NotesDAO
import com.lebartodev.lnote.data.entity.Note


@Database(entities = arrayOf(Note::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): NotesDAO
}