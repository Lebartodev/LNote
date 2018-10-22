package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.di.component.DaggerAppComponentMock
import io.mockk.MockKAnnotations
import io.mockk.every
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
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
        every { notesRepository.database.notesDao().getAll() } returns notesTest
    }

    @Test
    fun getNotes() {
        val testObserver = TestObserver<List<Note>>()
        notesRepository.getNotes().subscribe(testObserver)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(notesTest)
    }

    @Test
    fun createNote() {
    }
}