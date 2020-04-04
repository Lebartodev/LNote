package com.lebartodev.lnote.di.settings

import android.content.Context
import com.lebartodev.lnote.common.settings.SettingsBottomView
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.utils.SettingsScope
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