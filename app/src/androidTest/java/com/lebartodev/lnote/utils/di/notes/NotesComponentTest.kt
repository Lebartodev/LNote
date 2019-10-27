package com.lebartodev.lnote.utils.di.notes

import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.common.notes.NotesActivityInstrumentationTest
import com.lebartodev.lnote.repository.NotesDAOTest
import com.lebartodev.lnote.di.notes.NotesComponent
import com.lebartodev.lnote.di.notes.NotesModule
import dagger.Subcomponent

@Subcomponent(modules = [NotesModule::class])
interface NotesComponentTest : NotesComponent {
    override fun inject(activity: NotesActivity)
    fun inject(notesDAOTest: NotesDAOTest)
    fun inject(notesActivityInstrumentationTest: NotesActivityInstrumentationTest)
}