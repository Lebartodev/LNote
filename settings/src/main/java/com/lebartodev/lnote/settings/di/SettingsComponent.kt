package com.lebartodev.lnote.settings.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnote.settings.SettingsBottomView
import dagger.BindsInstance
import dagger.Component

@SettingsScope
@Component(dependencies = [AppComponent::class], modules = [SettingsModule::class])
interface SettingsComponent {
    fun inject(settingsBottomView: SettingsBottomView)

    @Component.Builder
    interface Builder {
        fun build(): SettingsComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}