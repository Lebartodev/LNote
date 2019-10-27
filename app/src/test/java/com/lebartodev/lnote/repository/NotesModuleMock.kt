package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.SchedulersFacade
import javax.inject.Named

class NotesModuleMock : NotesModule() {
    @Named("Real")
    override fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository =
            NotesRepository(database, schedulersFacade)
}
