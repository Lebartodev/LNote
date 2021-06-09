package com.lebartodev.lnote.utils.di.app

import android.app.Application
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.core.di.app.ManagersModule
import com.lebartodev.core.di.PreferencesModule
import com.lebartodev.core.data.repository.NotesDAOTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModuleTest::class, SchedulersModuleTest::class, ManagersModule::class, PreferencesModule::class])
interface AppComponentTest : AppComponent {

    fun inject(notesFragment: NotesDAOTest)

    @Component.Builder
    interface Builder {
        fun build(): AppComponentTest
        @BindsInstance
        fun applicationContext(application: Application): Builder
    }
}