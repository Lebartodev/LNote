package com.lebartodev.lnote.utils.di.notes

import com.lebartodev.lnote.common.details.EditNoteFragmentTest
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesActivityInstrumentationTest
import com.lebartodev.lnote.di.notes.NotesComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.repository.NotesDAOTest
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponentTest : NotesComponent {
    override fun inject(activity: NotesActivity)
    fun inject(notesDAOTest: NotesDAOTest)
    fun inject(notesActivityInstrumentationTest: NotesActivityInstrumentationTest)
    fun inject(editNoteFragmentTest: EditNoteFragmentTest)
}