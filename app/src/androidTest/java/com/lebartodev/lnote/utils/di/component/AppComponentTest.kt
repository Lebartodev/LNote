package com.lebartodev.lnote.utils.di.component

import android.app.Application
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesActivityTest
import com.lebartodev.lnote.utils.di.module.AppModuleTest
import com.lebartodev.lnote.common.repository.NotesDAOTest
import com.lebartodev.lnote.di.component.AppComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModuleTest::class])
interface AppComponentTest : AppComponent {
    fun inject(notesDAOTest: NotesDAOTest)
    fun inject(notesActivityTest: NotesActivityTest)
    override fun inject(mainActivity: NotesActivity)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withApplication(application: Application): Builder

        fun build(): AppComponentTest
    }
}