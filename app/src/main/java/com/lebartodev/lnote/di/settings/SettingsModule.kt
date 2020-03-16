package com.lebartodev.lnote.di.settings

import android.content.Context
import android.content.SharedPreferences
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.settings.SettingsViewModelFactory
import com.lebartodev.lnote.repository.SettingsRepository
import dagger.Module
import dagger.Provides

@Module
class SettingsModule(private val context: Context) {
    @Provides
    fun providePreferences(): SharedPreferences = context.getSharedPreferences(context.getString(R.string.settings_tag), Context.MODE_PRIVATE)

    @Provides
    fun provideNotesRepository(preferences: SharedPreferences): SettingsRepository = SettingsRepository(preferences)

    @Provides
    fun provideLNotesViewModelFactory(settingsRepository: SettingsRepository): SettingsViewModelFactory = SettingsViewModelFactory(settingsRepository)
}