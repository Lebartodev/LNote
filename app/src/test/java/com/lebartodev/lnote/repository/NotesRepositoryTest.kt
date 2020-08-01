package com.lebartodev.lnote.repository

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.dao.NotesDAO
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentMock
import com.lebartodev.lnote.utils.di.notes.DaggerNotesComponentMock
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

class NotesRepositoryTest {
    @Inject
    lateinit var schedulersFacade: SchedulersFacade
    @Inject
    lateinit var database: AppDatabase
    @Inject
    @field:Named("Real")
    lateinit var notesRepository: Repository.Notes
    val notesTest = listOf(
            Note(0, "title", null, System.currentTimeMillis(), "text"),
            Note(0, "title", null, System.currentTimeMillis(), "text"))

    @Before
    fun setUp() {
        val notesDao: NotesDAO = mock() {
            on { getAll() } doReturn Flowable.just(notesTest)
        }
        val comp = DaggerAppComponentMock.builder()
                .applicationContext(mock())
                .build()
        DaggerNotesComponentMock.builder().appComponent(comp).context(mock()).build().inject(this)

        whenever(database.notesDao()).thenReturn(notesDao)
    }

    @Test
    fun getNotes() {
        val testSubscriber = TestSubscriber<List<Note>>()
        notesRepository.getNotes().subscribe(testSubscriber)
        testSubscriber.assertComplete()
        testSubscriber.assertNoErrors()
        testSubscriber.assertValueCount(1)
        testSubscriber.assertValue(notesTest)
    }
}