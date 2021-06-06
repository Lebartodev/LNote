package com.lebartodev.lnote.utils.di.app

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModuleTest {
    @Provides
    @Singleton
    fun provideDatabaseMock(application: Application): AppDatabase = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).allowMainThreadQueries().build()
}