package com.lebartodev.lnote.utils.di.app

import android.app.Application
import com.lebartodev.lnote.common.details.EditNoteFragmentTest
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.app.ManagersModule
import com.lebartodev.lnote.di.app.PreferencesModule
import com.lebartodev.lnote.repository.NotesDAOTest
import com.lebartodev.lnote.utils.SchedulersFacade
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