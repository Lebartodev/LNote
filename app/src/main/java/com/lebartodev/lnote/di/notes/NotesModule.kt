package com.lebartodev.lnote.di.notes

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.repository.Repository
import com.lebartodev.lnote.repository.SettingsRepository
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface NotesModule {
    @Binds
    fun provideSettingsRepository(settingsRepository: SettingsRepository): Repository.Settings

    @Binds
    fun provideNotesRepository(notesRepository: NotesRepository): Repository.Notes

    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: LNoteViewModelFactory): ViewModelProvider.Factory
}