package com.lebartodev.lnote.repository

import android.app.Application
import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.data.dao.NotesDAO
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.SchedulersFacadeImpl
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentMock
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
    lateinit var schedulersFacade: SchedulersFacadeImpl
    @Inject
    lateinit var database: AppDatabase
    @Inject
    lateinit var notesRepository: NotesRepository
    val notesTest = listOf(
            Note(0, "title", null, System.currentTimeMillis(), "text"),
            Note(0, "title", null, System.currentTimeMillis(), "text"))

    @Before
    fun setUp() {
        val notesDao: NotesDAO = mock() {
            on { getAll() } doReturn Flowable.just(notesTest)
        }
//        val comp = DaggerAppComponentMock.builder()
//        comp.inject(mock<Application>())
//        comp.plus(NotesModuleMock()).inject(this)

        whenever(schedulersFacade.io()).thenReturn(Schedulers.trampoline())
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


//    private class NotesModuleMock : NotesModule() {
//        @Named("Real")
//        override fun provideNotesRepository(database: AppDatabase, schedulersFacade: SchedulersFacadeImpl): NotesRepository =
//                NotesRepository(database, schedulersFacade)
//    }
}