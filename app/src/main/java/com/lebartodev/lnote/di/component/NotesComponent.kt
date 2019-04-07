package com.lebartodev.lnote.di.component

import com.lebartodev.lnote.common.creation.NoteFragment
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesFragment
import com.lebartodev.lnote.di.module.NotesModule
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(activity: NotesActivity)
    fun inject(fragment: NotesFragment)
    fun inject(fragment: NoteFragment)
}