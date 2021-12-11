package com.lebartodev.lnote.feature_attach.di

import androidx.lifecycle.ViewModel
import com.lebartodev.core.di.app.FactoryModule
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.core.di.utils.ViewModelKey
import com.lebartodev.lnote.feature_attach.FilesRepository
import com.lebartodev.lnote.feature_attach.FilesRepositoryImpl
import com.lebartodev.lnote.feature_attach.ui.AttachViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelsModule::class, FactoryModule::class, RepositoryModule::class])
interface AttachModule

@Module
interface ViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(AttachViewModel::class)
    fun demoViewModel(viewModel: AttachViewModel): ViewModel
}

@Module
interface RepositoryModule {
    @FeatureScope
    @Binds
    fun bindRepository(factory: FilesRepositoryImpl): FilesRepository
}