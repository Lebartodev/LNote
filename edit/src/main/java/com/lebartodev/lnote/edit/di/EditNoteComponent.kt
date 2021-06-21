package com.lebartodev.lnote.edit.di

import android.content.Context
import com.lebartodev.core.di.app.AppComponent
import com.lebartodev.lnote.edit.EditNoteContainerFragment
import com.lebartodev.lnote.edit.EditNoteFragment
import com.lebartodev.lnote.edit.creation.NoteCreationView
import dagger.BindsInstance
import dagger.Component

@EditScope
@Component(dependencies = [AppComponent::class], modules = [EditNoteModule::class])
interface EditNoteComponent {
    fun inject(editNoteFragment: EditNoteFragment)
    fun inject(noteCreationView: NoteCreationView)
    fun inject(editNoteContainerFragment: EditNoteContainerFragment)

    @Component.Builder
    interface Builder {
        fun build(): EditNoteComponent

        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}