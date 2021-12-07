package com.lebartodev.lnotes.list.di

import androidx.lifecycle.ViewModel
import com.lebartodev.core.di.app.FactoryModule
import com.lebartodev.core.di.utils.ViewModelKey
import com.lebartodev.lnotes.list.NotesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelsModule::class, FactoryModule::class])
interface ListModule

@Module
interface ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(NotesViewModel::class)
    fun notesViewModel(viewModel: NotesViewModel): ViewModel
}