package com.lebartodev.lnote.di.notes

import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.common.edit.NoteCreationView
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesFragment
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(activity: NotesActivity)
    fun inject(fragment: NotesFragment)
    fun inject(fragment: EditNoteFragment)
    fun inject(noteCreationView: NoteCreationView)
    fun inject(fragment: ShowNoteFragment)
}