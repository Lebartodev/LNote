package com.lebartodev.lnote.di.component

import android.app.Application
import com.lebartodev.lnote.di.module.AppModuleMock
import com.lebartodev.lnote.repository.NotesDAOTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModuleMock::class])
interface AppComponentMock : AppComponent {
    fun inject(notesRepositoryTest: NotesDAOTest)
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun withApplication(application: Application): Builder

        fun build(): AppComponentMock
    }
}