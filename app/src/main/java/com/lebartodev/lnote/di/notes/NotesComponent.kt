package com.lebartodev.lnote.di.notes

import android.content.Context
import com.lebartodev.lnote.common.archive.ArchiveFragment
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.common.notes.NotesFragment
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.utils.NotesScope
import dagger.BindsInstance
import dagger.Component

@NotesScope
@Component(dependencies = [AppComponent::class], modules = [NotesModule::class])
interface NotesComponent {
    fun inject(notesFragment: NotesFragment)
    fun inject(archiveFragment: ArchiveFragment)
    fun inject(editNoteFragment: EditNoteFragment)
    fun inject(showNoteFragment: ShowNoteFragment)

    @Component.Builder
    interface Builder {
        fun build(): NotesComponent
        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: AppComponent): Builder
    }
}