package com.lebartodev.lnote.di.module

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Singleton
@Module
class AppModuleMock {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase = Mockito.mock(AppDatabase::class.java)

    @Provides
    @Singleton
    fun provideSchedulersFacade(): SchedulersFacade {
        val result: SchedulersFacade = Mockito.mock(SchedulersFacade::class.java)
        return result
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