package com.lebartodev.lnote.utils.di.app

import android.app.Application
import android.content.SharedPreferences
import com.lebartodev.lnote.di.app.*
import com.lebartodev.lnote.utils.SchedulersFacade
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