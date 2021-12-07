package com.lebartodev.lnote.utils.di.app

import android.app.Application
import android.content.SharedPreferences
import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.app.ManagersModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DatabaseModuleMock::class, SchedulersModuleMock::class, ManagersModule::class, PreferencesModuleMock::class])
interface CoreComponentMock : CoreComponent {
    fun sharedPreferences(): SharedPreferences
    @Component.Builder
    interface Builder {
        fun build(): CoreComponentMock
        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}