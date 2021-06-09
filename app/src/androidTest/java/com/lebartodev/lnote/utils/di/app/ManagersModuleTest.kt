package com.lebartodev.lnote.utils.di.app

import com.lebartodev.core.data.CurrentNoteManager
import com.lebartodev.core.data.Manager
import com.lebartodev.lnote.settings.SettingsManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface ManagersModuleTest {
    @Binds
    @Singleton
    fun provideCurrentNoteManager(currentNoteManager: CurrentNoteManager): Manager.CurrentNote

    @Binds
    @Singleton
    fun provideSettingsManager(settingsManager: com.lebartodev.lnote.settings.SettingsManager): Manager.Settings
}