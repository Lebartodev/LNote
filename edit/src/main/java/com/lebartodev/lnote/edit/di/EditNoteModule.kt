package com.lebartodev.lnote.edit.di

import androidx.lifecycle.ViewModel
import com.lebartodev.core.di.app.FactoryModule
import com.lebartodev.core.di.utils.ViewModelKey
import com.lebartodev.lnote.edit.NoteEditViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelsModule::class, FactoryModule::class])
interface EditNoteModule

@Module
interface ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(NoteEditViewModel::class)
    fun demoViewModel(viewModel: NoteEditViewModel): ViewModel
}