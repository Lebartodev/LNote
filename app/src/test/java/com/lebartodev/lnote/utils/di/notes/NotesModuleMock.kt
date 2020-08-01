package com.lebartodev.lnote.utils.di.notes

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.repository.Repository
import com.lebartodev.lnote.repository.SettingsRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import com.nhaarman.mockitokotlin2.mock
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class NotesModuleMock {
    @Provides
    fun provideSettingsRepository(): Repository.Settings = mock()

    @Provides
    fun provideNotesRepository(): Repository.Notes = mock()

    @Provides
    fun provideLNotesViewModelFactory(): ViewModelProvider.Factory = mock()

    @Named("Real")
    @Provides
    fun provideRealNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacade): Repository.Notes = NotesRepository(database, schedulersFacade)
}