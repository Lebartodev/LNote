package com.lebartodev.lnote.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.lebartodev.lnote.common.LNoteApplicationMock
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.AppComponentTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class NotesDAOTest {
    @Inject
    protected lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as LNoteApplicationMock
        val component = app.component() as AppComponentTest
        component.inject(this)
        database.clearAllTables()
    }

    @Test
    fun getAll() {
        val notes = database.notesDao().getAll()
        assertTrue(notes.isEmpty())
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert() {
        var notes = database.notesDao().getAll()
        assert(notes.isEmpty())
        database.notesDao().insert(Note(1000L, "Title", System.currentTimeMillis(), "Text"))
        database.notesDao().insert(Note(2000L, "Title", System.currentTimeMillis(), "Text"))
        notes = database.notesDao().getAll()
        assertEquals(notes.size, 2)
        assertEquals(notes[0].id, 1000L)
        assertEquals(notes[1].id, 2000L)
        database.notesDao().insert(Note(1000L, "Title", System.currentTimeMillis(), "Text"))
    }

    @Test
    fun update() {
        val notes = database.notesDao().getAll()
        assert(notes.isEmpty())
        val noteId = database.notesDao().insert(Note(null, "Title", System.currentTimeMillis(), "Text"))
        val note = database.notesDao().getById(noteId)
        note.text = "New Text"
        note.title = "New Title"
        database.notesDao().update(note)
        val newNote = database.notesDao().getById(noteId)
        assertEquals(newNote.text, "New Text")
        assertEquals(newNote.title, "New Title")
        assertTrue(database.notesDao().getAll().isNotEmpty())
    }

    @Test
    fun delete() {
        val noteId = database.notesDao().insert(Note(null, "Title", System.currentTimeMillis(), "Text"))
        val note = database.notesDao().getById(noteId)
        database.notesDao().delete(note)
        assertTrue(database.notesDao().getAll().isEmpty())
    }
}