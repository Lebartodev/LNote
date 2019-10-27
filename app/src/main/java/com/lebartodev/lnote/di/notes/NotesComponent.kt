package com.lebartodev.lnote.di.notes

import com.lebartodev.lnote.common.details.EditNoteFragment
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesFragment
import com.lebartodev.lnote.di.notes.NotesModule
import dagger.Component
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(activity: NotesActivity)
    fun inject(fragment: NotesFragment)
    fun inject(fragment: EditNoteFragment)
}