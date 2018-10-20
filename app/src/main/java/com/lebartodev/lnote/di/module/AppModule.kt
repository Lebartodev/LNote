package com.lebartodev.lnote.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
open class AppModule {
    @Provides
    open fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "database.db").build()
    }

    @Provides
    fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository {
        return NotesRepository(database, schedulersFacade)
    }

    @Provides
    fun provideSchedulersFacade(): SchedulersFacade {
        return SchedulersFacade()
    }
}