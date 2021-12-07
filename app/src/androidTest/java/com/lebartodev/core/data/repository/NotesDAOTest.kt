package com.lebartodev.core.data.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.test.platform.app.InstrumentationRegistry
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.di.app.CoreComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import io.reactivex.Completable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import javax.inject.Inject


class NotesDAOTest {
    @Inject
    internal lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as LNoteApplicationMock
        val component = app.coreComponent as CoreComponentTest
        component.inject(this)
        database.clearAllTables()
    }

    @Test(expected = SQLiteConstraintException::class)
    fun insert() {
        val noteId1 = database.notesDao().insert(Note(1000L, "Title", null, 2L, "Text"))
        val noteId2 = database.notesDao().insert(Note(2000L, "Title", null, 1L, "Text"))
        val testSubscriber = database.notesDao().getAll().test()
        testSubscriber.awaitCount(1)
        val notes = testSubscriber.values()[0]
        assertEquals(testSubscriber.values()[0].size, 2)
        assertEquals(notes[0].id, 1000L)
        assertEquals(notes[1].id, 2000L)
        database.notesDao().insert(Note(1000L, "Title", null, System.currentTimeMillis(), "Text"))
    }

    @Test
    fun update() {
        val testSubscriber = database.notesDao().getAll().test()
        testSubscriber.awaitCount(1)
        assertEquals(testSubscriber.values()[0].size, 0)
        val noteId = database.notesDao().insert(Note(null, "Title", null, System.currentTimeMillis(), "Text"))
        val noteSubscriber = database.notesDao().getById(noteId)
                .map { note ->
                    note.text = "New Text"
                    note.title = "New Title"
                    note
                }
                .flatMapCompletable { note ->
                    Completable.fromAction { database.notesDao().update(note) }
                }
                .test()
        noteSubscriber.awaitCount(1)
        val newNoteSubscriber = database.notesDao().getById(noteId)
                .test()
        newNoteSubscriber.awaitCount(1)
        val newNote = newNoteSubscriber.values().last()
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
        val testSubscriberDelete = database.notesDao().getById(noteId)
                .flatMapCompletable { note ->
                    Completable.fromAction { database.notesDao().delete(note) }
                }
                .test()
        testSubscriberDelete.awaitCount(1)
        testSubscriber.awaitCount(1)
        assertTrue(testSubscriber.values().last().isEmpty())
    }
}