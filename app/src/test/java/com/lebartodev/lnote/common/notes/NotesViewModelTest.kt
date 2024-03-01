package com.lebartodev.lnote.common.notes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentMock
import com.lebartodev.lnote.utils.di.notes.DaggerNotesComponentMock
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import javax.inject.Inject


@RunWith(JUnit4::class)
class NotesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var schedulersFacade: SchedulersFacade

    @Inject
    lateinit var notesRepository: Repository.Notes
    lateinit var notesViewModel: com.lebartodev.lnotes.list.NotesViewModel

    @Before
    fun setUp() {
        val comp = DaggerAppComponentMock.builder()
            .applicationContext(mock())
            .build()
        DaggerNotesComponentMock.builder().appComponent(comp).context(mock()).build().inject(this)


        whenever(notesRepository.getNotes()).thenReturn(Flowable.just(arrayListOf()))
        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
            Single.just(1L)
        )
        notesViewModel =
            com.lebartodev.lnotes.list.NotesViewModel(notesRepository, schedulersFacade)
    }

    @Test
    fun loadNotes() {
        val loadNotesObserver: Observer<List<Note>> = mock()
        notesViewModel.getNotes().observeForever(loadNotesObserver)
        verify(loadNotesObserver).onChanged(arrayListOf())
        notesViewModel.getNotes().removeObserver(loadNotesObserver)

    }

    @Suppress("Detekt.ForbiddenComment")
    @Test
    fun loadNotesWithError() {
        val exception = NullPointerException()
        val loadNotesObserver: Observer<List<Note>> = mock()
        whenever(notesRepository.getNotes()).thenReturn(Flowable.error(exception))
        notesViewModel.getNotes().observeForever(loadNotesObserver)
        notesViewModel.fetchNotes()
        //TODO: verify(loadNotesObserver).onChanged(error)
    }
}