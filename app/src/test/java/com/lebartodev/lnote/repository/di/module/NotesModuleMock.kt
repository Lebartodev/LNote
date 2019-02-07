package com.lebartodev.lnote.repository.di.module

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade

class NotesModuleMock : NotesModule() {

    override fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository =
            NotesRepository(database, schedulersFacade)
}
