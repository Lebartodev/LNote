package com.lebartodev.lnote.di

import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.common.notes.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NotesViewModel::class)
    internal abstract fun notesViewModule(userViewModel: NotesViewModel): ViewModel
}