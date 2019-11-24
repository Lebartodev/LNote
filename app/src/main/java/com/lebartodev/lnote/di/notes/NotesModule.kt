package com.lebartodev.lnote.di.notes

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import dagger.Module
import dagger.Provides

@DebugOpenClass
@Module
class NotesModule {
    @Provides
    fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository = NotesRepository(database, schedulersFacade)

    @Provides
    fun provideLNotesViewModelFactory(notesRepository: NotesRepository, schedulersFacade: SchedulersFacade): LNoteViewModelFactory = LNoteViewModelFactory(notesRepository, schedulersFacade)
}