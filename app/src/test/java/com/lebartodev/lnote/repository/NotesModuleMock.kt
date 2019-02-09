package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock
import javax.inject.Named

class NotesModuleMock : NotesModule() {
    @Named("Real")
    override fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): NotesRepository =
            NotesRepository(database, schedulersFacade)
}
