package com.lebartodev.lnote.utils.di.notes

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.core.data.repository.NotesRepository
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.mock
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
    fun provideRealNotesRepository(
        database: AppDatabase,
        schedulersFacade: SchedulersFacade
    ): Repository.Notes = NotesRepository(database, schedulersFacade)
}