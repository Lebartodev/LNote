package com.lebartodev.lnotes.list.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnotes.list.ListNotesViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ListModule {
    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: ListNotesViewModelFactory): ViewModelProvider.Factory
}