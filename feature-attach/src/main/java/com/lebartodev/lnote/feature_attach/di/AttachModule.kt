package com.lebartodev.lnote.feature_attach.di

import androidx.lifecycle.ViewModel
import com.lebartodev.core.di.app.FactoryModule
import com.lebartodev.core.di.utils.ViewModelKey
import com.lebartodev.lnote.feature_attach.ui.AttachViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelsModule::class, FactoryModule::class])
interface AttachModule

@Module
interface ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(AttachViewModel::class)
    fun demoViewModel(viewModel: AttachViewModel): ViewModel
}