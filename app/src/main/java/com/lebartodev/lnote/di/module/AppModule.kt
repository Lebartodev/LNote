package com.lebartodev.lnote.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.ViewModelModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application,
                AppDatabase::class.java, "database.db").build()
    }
}