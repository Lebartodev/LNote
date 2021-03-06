package com.lebartodev.lnote.di.app

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()
}