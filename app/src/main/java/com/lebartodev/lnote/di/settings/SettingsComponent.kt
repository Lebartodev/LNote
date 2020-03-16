package com.lebartodev.lnote.di.settings

import com.lebartodev.lnote.common.settings.SettingsBottomView
import dagger.Component
import dagger.Subcomponent

@Component(modules = [SettingsModule::class])
interface SettingsComponent {
    fun inject(view: SettingsBottomView)
}