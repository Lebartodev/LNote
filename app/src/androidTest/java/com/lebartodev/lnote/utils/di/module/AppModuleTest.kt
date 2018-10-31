package com.lebartodev.lnote.utils.di.module

import android.app.Application
import androidx.room.Room
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.mocks.LNoteViewModelFactoryMock
import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class AppModuleTest {
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideSchedulersFacade() = mock<SchedulersFacade>()

    @Provides
    @Singleton
    fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository {
        return NotesRepository(database, schedulersFacade)
    }

    @Provides
    @Singleton
    fun provideLNotesViewModelFactory(notesRepository: NotesRepository): LNoteViewModelFactory {
        return LNoteViewModelFactoryMock(notesRepository)
    }
}