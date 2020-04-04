package com.lebartodev.lnote.di.settings

import androidx.lifecycle.ViewModelProvider
import com.lebartodev.lnote.common.settings.SettingsViewModelFactory
import com.lebartodev.lnote.repository.Repository
import com.lebartodev.lnote.repository.SettingsRepository
import dagger.Binds
import dagger.Module

@Module
interface SettingsModule {
    @Binds
    fun provideSettingsRepository(settingsRepository: SettingsRepository): Repository.Settings

    @Binds
    fun provideSettingsViewModelFactory(viewModel: SettingsViewModelFactory): ViewModelProvider.Factory
}