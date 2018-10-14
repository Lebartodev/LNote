package com.lebartodev.lnote.application.component

import com.lebartodev.lnote.application.module.NotesModule
import com.lebartodev.lnote.notes.NotesActivity
import com.lebartodev.lnote.notes.NotesPresenter
import dagger.Component


@Component(modules = [NotesModule::class])
interface NotesComponent {
    fun inject(notesActivity: NotesActivity)
    fun getNotesPresenter(): NotesPresenter
}