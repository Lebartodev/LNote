package com.lebartodev.lnote.common.notes

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock

class NotesModuleRepositoryMock : NotesModule() {
    override fun provideNotesRepository(database: AppDatabase,
                                        schedulersFacade: SchedulersFacade): NotesRepository = mock()
}
