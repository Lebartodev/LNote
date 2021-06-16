package com.lebartodev.lnote.archive.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.archive.ArchiveViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ArchiveModule {
    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: ArchiveViewModelFactory): ViewModelProvider.Factory
}