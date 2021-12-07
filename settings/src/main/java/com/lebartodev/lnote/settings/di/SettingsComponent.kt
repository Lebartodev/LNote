package com.lebartodev.lnote.settings.di

import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnote.settings.SettingsBottomView
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class], modules = [SettingsModule::class])
interface SettingsComponent {
    fun inject(settingsBottomView: SettingsBottomView)
}