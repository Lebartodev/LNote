package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.DaggerAppComponentMock
import io.mockk.MockKAnnotations
import io.mockk.every
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class NotesRepositoryTest {
    @Inject
    lateinit var notesRepository: NotesRepository
    val notesTest = listOf(
            Note(0, "title", System.currentTimeMillis(), "text"),
            Note(0, "title", System.currentTimeMillis(), "text"))

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        DaggerAppComponentMock.builder().build().inject(this)
        every { notesRepository.schedulersFacade.io() } returns Schedulers.trampoline()
        every { notesRepository.database.notesDao().getAll() } returns Flowable.just(notesTest)
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