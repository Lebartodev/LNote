package com.lebartodev.lnote.feature_attach.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.feature_attach.AttachViewModelFactory
import com.lebartodev.lnote.feature_attach.FilesRepository
import com.lebartodev.lnote.feature_attach.FilesRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface AttachModule {
    @Binds
    fun provideViewModel(factory: AttachViewModelFactory): ViewModelProvider.Factory

    @Binds
    fun provideFilesRepository(filesRepository: FilesRepositoryImpl): FilesRepository
}