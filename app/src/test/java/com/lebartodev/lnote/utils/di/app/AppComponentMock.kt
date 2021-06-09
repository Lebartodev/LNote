package com.lebartodev.lnote.utils.di.app

import android.app.Application
import android.content.SharedPreferences
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.core.di.app.ManagersModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DatabaseModuleMock::class, SchedulersModuleMock::class, ManagersModule::class, PreferencesModuleMock::class])
interface AppComponentMock : AppComponent {
    fun sharedPreferences(): SharedPreferences
    @Component.Builder
    interface Builder {
        fun build(): AppComponentMock
        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}