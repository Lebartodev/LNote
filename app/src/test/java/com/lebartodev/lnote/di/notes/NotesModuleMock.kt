package com.lebartodev.lnote.di.notes

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock

class NotesModuleMock : NotesModule() {
    override fun provideNotesRepository(database: AppDatabase,
                                        schedulersFacade: SchedulersFacade): NotesRepository = mock()
}
