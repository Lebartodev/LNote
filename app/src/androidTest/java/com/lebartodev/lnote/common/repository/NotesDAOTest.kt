package com.lebartodev.lnote.common.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.di.component.AppComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class NotesDAOTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
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

    @Test(expected = SQLiteConstraintException::class)
    fun insert() {
        val l = database.notesDao().insert(Note(1000L, "Title", null, System.currentTimeMillis(), "Text"))
        val l2 = database.notesDao().insert(Note(2000L, "Title", null, System.currentTimeMillis(), "Text"))
        val testSubscriber = database.notesDao().getAll().test()
        testSubscriber.awaitCount(1)
        val notes = testSubscriber.values()[0]
        assertEquals(testSubscriber.values()[0].size, 2)
        assertEquals(notes[0].id, 1000L)
        assertEquals(notes[1].id, 2000L)
        database.notesDao().insert(Note(1000L, "Title",null,  System.currentTimeMillis(), "Text"))
    }

    @Test
    fun update() {
        val testSubscriber = database.notesDao().getAll().test()
        testSubscriber.awaitCount(1)
        assertEquals(testSubscriber.values()[0].size, 0)
        val noteId = database.notesDao().insert(Note(null, "Title",null,  System.currentTimeMillis(), "Text"))
        val note = database.notesDao().getById(noteId)
        note.text = "New Text"
        note.title = "New Title"
        database.notesDao().update(note)
        val newNote = database.notesDao().getById(noteId)
        assertEquals(newNote.text, "New Text")
        assertEquals(newNote.title, "New Title")
        assertTrue(testSubscriber.values().last().isNotEmpty())
    }

    @Test
    fun delete() {
        val testSubscriber = database.notesDao().getAll().test()
        testSubscriber.awaitCount(1)
        assertEquals(testSubscriber.values().last().size, 0)
        val noteId = database.notesDao().insert(Note(null, "Title", null, System.currentTimeMillis(), "Text"))
        val note = database.notesDao().getById(noteId)
        database.notesDao().delete(note)
        testSubscriber.awaitCount(1)
        assertTrue(testSubscriber.values().last().isEmpty())
    }
}