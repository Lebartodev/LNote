package com.lebartodev.lnote.utils.di.app

import android.app.Application
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.app.ManagersModule
import com.lebartodev.lnote.di.app.PreferencesModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DatabaseModuleMock::class, SchedulersModuleMock::class, ManagersModule::class, PreferencesModule::class])
interface AppComponentMock : AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponentMock
        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}