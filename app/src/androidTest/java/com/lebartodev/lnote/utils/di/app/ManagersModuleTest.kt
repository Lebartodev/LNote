package com.lebartodev.lnote.utils.di.app

import com.lebartodev.lnote.data.CurrentNoteManager
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.data.SettingsManager
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
    fun provideSettingsManager(settingsManager: SettingsManager): Manager.Settings
}