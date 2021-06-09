package com.lebartodev.lnote.utils.di.notes

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.core.data.repository.NotesRepository
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.data.repository.SettingsRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface NotesModuleTest {
    @Binds
    fun provideSettingsRepository(settingsRepository: SettingsRepository): Repository.Settings

    @Binds
    fun provideNotesRepository(notesRepository: NotesRepository): Repository.Notes

    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: LNoteViewModelFactory): ViewModelProvider.Factory
}