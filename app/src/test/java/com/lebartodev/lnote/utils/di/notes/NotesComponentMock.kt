package com.lebartodev.lnote.utils.di.notes

import android.content.Context
import com.lebartodev.lnote.common.details.NoteEditViewModelTest
import com.lebartodev.lnote.common.notes.NotesViewModelTest
import com.lebartodev.lnote.show.di.ShowNoteScope
import com.lebartodev.core.data.repository.NotesRepositoryTest
import com.lebartodev.lnote.utils.di.app.AppComponentMock
import dagger.BindsInstance
import dagger.Component

@ShowNoteScope
@Component(dependencies = [AppComponentMock::class], modules = [NotesModuleMock::class])
interface NotesComponentMock {
    fun inject(noteEditViewModelTest: NoteEditViewModelTest)
    fun inject(noteEditViewModelTest: NotesViewModelTest)
    fun inject(notesRepositoryTest: NotesRepositoryTest)

    @Component.Builder
    interface Builder {
        fun build(): NotesComponentMock
        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponentMock): Builder
    }
}