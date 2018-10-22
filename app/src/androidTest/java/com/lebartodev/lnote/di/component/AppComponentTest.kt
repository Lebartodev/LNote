package com.lebartodev.lnote.di.component

import android.app.Application
import com.lebartodev.lnote.di.module.AppModuleTest
import com.lebartodev.lnote.repository.NotesDAOTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModuleTest::class])
interface AppComponentTest : AppComponent {
    fun inject(notesDAOTest: NotesDAOTest)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withApplication(application: Application): Builder

        fun build(): AppComponentTest
    }
}