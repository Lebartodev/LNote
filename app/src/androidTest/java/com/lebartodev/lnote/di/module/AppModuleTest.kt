package com.lebartodev.lnote.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class AppModuleTest {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): AppDatabase {
        return Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).build()
    }
}