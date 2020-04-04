package com.lebartodev.lnote.utils.di.app

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.app.AppModule

class AppModuleTest(application: Application) : AppModule(application) {
    override fun provideDatabase(application: Application): AppDatabase = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).allowMainThreadQueries().build()
}