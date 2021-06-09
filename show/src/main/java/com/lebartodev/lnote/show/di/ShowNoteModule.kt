package com.lebartodev.lnote.show.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.show.ShowNoteViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface ShowNoteModule {
    @Binds
    fun provideLNotesViewModelFactory(lNoteViewModelFactory: ShowNoteViewModelFactory): ViewModelProvider.Factory
}