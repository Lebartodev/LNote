package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.DaggerAppComponentMock
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import javax.inject.Inject

class NotesRepositoryTest {
    @Inject
    lateinit var notesRepository: NotesRepository
    val notesTest = listOf(
            Note(0, "title", System.currentTimeMillis(), "text"),
            Note(0, "title", System.currentTimeMillis(), "text"))

    @Before
    fun setUp() {
        DaggerAppComponentMock.builder().build().inject(this)
        `when`(notesRepository.schedulersFacade.io()).thenReturn(Schedulers.trampoline())
        doReturn(Flowable.just(notesTest)).`when`(notesRepository.database.notesDao()).getAll()
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

    @Test
    fun createNote() {
    }
}