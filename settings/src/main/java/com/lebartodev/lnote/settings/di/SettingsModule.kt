package com.lebartodev.lnote.settings.di

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.settings.SettingsViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface SettingsModule {
    @Binds
    fun provideSettingsViewModelFactory(viewModel: SettingsViewModelFactory): ViewModelProvider.Factory
}