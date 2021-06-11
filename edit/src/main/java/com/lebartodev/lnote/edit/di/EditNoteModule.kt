package com.lebartodev.lnote.edit.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.edit.EditNoteViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface EditNoteModule {
    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: EditNoteViewModelFactory): ViewModelProvider.Factory
}