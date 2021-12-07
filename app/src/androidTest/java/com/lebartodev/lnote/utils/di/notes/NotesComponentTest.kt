package com.lebartodev.lnote.utils.di.notes

import android.content.Context
import com.lebartodev.lnote.common.details.EditNoteFragmentTest
import com.lebartodev.lnote.common.notes.NotesActivityInstrumentationTest
import com.lebartodev.lnote.show.di.ShowNoteScope
import com.lebartodev.lnote.utils.di.app.CoreComponentTest
import dagger.BindsInstance
import dagger.Component

@ShowNoteScope
@Component(dependencies = [CoreComponentTest::class], modules = [NotesModuleTest::class])
interface NotesComponentTest {
    fun inject(notesFragment: EditNoteFragmentTest)
    fun inject(notesActivity: NotesActivityInstrumentationTest)

    @Component.Builder
    interface Builder {
        fun build(): NotesComponentTest
        @BindsInstance
        fun context(context: Context): Builder

        fun appComponent(appComponent: CoreComponentTest): Builder
    }
}