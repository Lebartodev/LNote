package com.lebartodev.lnote.di.module

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Module
class AppModuleMock {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = mock()

    @Provides
    @Singleton
    fun provideSchedulersFacade(): SchedulersFacade {
        return mock()
    }

    @Provides
    fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository {
        return NotesRepository(database, schedulersFacade)
    }

    @Provides
    fun provideLNotesViewModelFactory(notesRepository: NotesRepository): LNoteViewModelFactory {
        return LNoteViewModelFactory(notesRepository)
    }


}