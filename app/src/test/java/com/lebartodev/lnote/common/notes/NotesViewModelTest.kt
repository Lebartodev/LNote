package com.lebartodev.lnote.common.notes

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.di.component.DaggerAppComponentMock
import com.lebartodev.lnote.di.module.AppModuleMock
import com.lebartodev.lnote.repository.NotesRepository
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import javax.inject.Inject

@RunWith(JUnit4::class)
class NotesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var notesRepository: NotesRepository
    lateinit var notesViewModel: NotesViewModel

    val calendarObserver: Observer<Calendar?> = mock()

    @Before
    fun setUp() {
        val comp = DaggerAppComponentMock.builder().appModule(AppModuleMock(mock())).build()
        comp.inject(mock<Application>())
        comp.plus(NotesModuleRepositoryMock()).inject(this)
        notesViewModel = NotesViewModel(notesRepository)

        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Single.just(1L))
    }

    @Test
    fun setDate() {
        notesViewModel.selectedDate.observeForever(calendarObserver)
        notesViewModel.setDate(1996, 5, 5)
        assert(notesViewModel.selectedDate.value != null)
        assertThat(notesViewModel.selectedDate.value?.get(Calendar.YEAR), equalTo(1996))
        assertThat(notesViewModel.selectedDate.value?.get(Calendar.MONTH), equalTo(5))
        assertThat(notesViewModel.selectedDate.value?.get(Calendar.DAY_OF_MONTH), equalTo(5))
    }

    @Test
    fun clearDate() {
        notesViewModel.selectedDate.observeForever(calendarObserver)
        notesViewModel.clearDate()
        verify(calendarObserver).onChanged(null)
    }

    @Test
    fun selectedDateString() {
        val dateStringObserver: Observer<String> = mock()
        notesViewModel.selectedDateString().observeForever(dateStringObserver)
        notesViewModel.setDate(1996, 4, 5)
        verify(dateStringObserver).onChanged("Sun, 05 May 1996")
        notesViewModel.clearDate()
        verify(dateStringObserver).onChanged("")
    }

    @Test
    fun saveNote() {
        val saveNoteObserver: Observer<ViewModelObject<Long>> = mock()

        notesViewModel.saveNote("title", "text").observeForever(saveNoteObserver)
        verify(notesRepository).createNote("title", "text", null)
        verify(saveNoteObserver).onChanged(ViewModelObject.success(1L))

        val saveNoteObserver2: Observer<ViewModelObject<Long>> = mock()
        notesViewModel.setDate(1996, 4, 5)
        notesViewModel.saveNote("title", "text").observeForever(saveNoteObserver2)
        verify(notesRepository).createNote("title", "text", notesViewModel.selectedDate.value?.timeInMillis)
        verify(saveNoteObserver2).onChanged(ViewModelObject.success(1L))


        val exception = NullPointerException()
        val saveNoteObserver3: Observer<ViewModelObject<Long>> = mock()
        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
                Single.error(exception))
        notesViewModel.setDate(1996, 4, 5)
        notesViewModel.saveNote("title", null).observeForever(saveNoteObserver3)
        verify(notesRepository).createNote("title", null, notesViewModel.selectedDate.value?.timeInMillis)
        verify(saveNoteObserver3).onChanged(ViewModelObject.error(exception, null))

    }

    @Test
    fun loadNotes() {
        val exception = NullPointerException()
        val loadNotesObserver: Observer<ViewModelObject<List<Note>>> = mock()
        whenever(notesRepository.getNotes()).thenReturn(Flowable.just(arrayListOf()))

        notesViewModel.loadNotes().observeForever(loadNotesObserver)
        verify(loadNotesObserver).onChanged(ViewModelObject.success(arrayListOf()))
        notesViewModel.loadNotes().removeObserver(loadNotesObserver)


        whenever(notesRepository.getNotes()).thenReturn(Flowable.error(exception))
        notesViewModel.loadNotes().observeForever(loadNotesObserver)
        verify(loadNotesObserver).onChanged(ViewModelObject.error(exception, arrayListOf()))

    }

    @Test
    fun onDescriptionChanged() {
        val loadNotesObserver: Observer<String?> = mock()
        notesViewModel.descriptionTextLiveData.observeForever(loadNotesObserver)

        notesViewModel.onDescriptionChanged("1234567890")
        verify(loadNotesObserver).onChanged("1234567890")

        notesViewModel.onDescriptionChanged("123456789012345678901234567890")
        verify(loadNotesObserver).onChanged("123456789012345678901234")


    }
}