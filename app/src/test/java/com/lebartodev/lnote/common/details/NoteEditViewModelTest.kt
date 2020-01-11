package com.lebartodev.lnote.common.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.lebartodev.lnote.common.edit.NoteEditViewModel
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.di.app.AppModuleMock
import com.lebartodev.lnote.di.app.DaggerAppComponentMock
import com.lebartodev.lnote.di.notes.NotesModuleMock
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import javax.inject.Inject

class NoteEditViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Inject
    lateinit var schedulersFacade: SchedulersFacade
    @Inject
    lateinit var notesRepository: NotesRepository
    lateinit var editViewModel: NoteEditViewModel

    val calendarObserver: Observer<Long?> = mock()

    @Before
    fun setUp() {
        val comp = DaggerAppComponentMock.builder()
                .appModule(AppModuleMock(mock()))
                .build()
        comp.inject(mock())
        comp.plus(NotesModuleMock()).inject(this)

        editViewModel = NoteEditViewModel(notesRepository, schedulersFacade)

        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Single.just(1L))
    }

    @Test
    fun setDate() {
        editViewModel.selectedDate().observeForever(calendarObserver)
        editViewModel.setDate(1996, 5, 5)
        assert(editViewModel.selectedDate().value != null)
        val date = Calendar.getInstance()
        date.timeInMillis = editViewModel.selectedDate().value ?: 0

        MatcherAssert.assertThat(date.get(Calendar.YEAR), CoreMatchers.equalTo(1996))
        MatcherAssert.assertThat(date.get(Calendar.MONTH), CoreMatchers.equalTo(5))
        MatcherAssert.assertThat(date.get(Calendar.DAY_OF_MONTH), CoreMatchers.equalTo(5))
    }

    @Test
    fun clearDate() {
        editViewModel.selectedDate().observeForever(calendarObserver)
        editViewModel.clearDate()
        verify(calendarObserver).onChanged(null)
    }

    @Test
    fun saveNote() {
        val saveNoteObserver: Observer<ViewModelObject<Long>> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.saveNote("title", "text")
        verify(notesRepository).createNote("title", "text", null)
        verify(saveNoteObserver).onChanged(ViewModelObject.success(1L))
    }

    @Test
    fun saveNoteWithDate() {
        val saveNoteObserver: Observer<ViewModelObject<Long>> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.setDate(1996, 4, 5)
        editViewModel.saveNote("title", "text")
        verify(notesRepository).createNote("title", "text", editViewModel.selectedDate().value)
        verify(saveNoteObserver).onChanged(ViewModelObject.success(1L))
    }

    @Test
    fun saveNoteWithError() {
        val exception = NullPointerException()
        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
                Single.error(exception))

        val saveNoteObserver: Observer<ViewModelObject<Long>> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.setDate(1996, 4, 5)
        editViewModel.saveNote("title", null)
        verify(notesRepository).createNote("title", null, editViewModel.selectedDate().value)
        verify(saveNoteObserver).onChanged(ViewModelObject.error(exception, null))
    }


    @Test
    fun onDescriptionChanged() {
        val loadNotesObserver: Observer<String?> = mock()
        editViewModel.descriptionTextLiveData.observeForever(loadNotesObserver)

        editViewModel.setDescription("1234567890")
        verify(loadNotesObserver).onChanged("1234567890")

        editViewModel.setDescription("123456789012345678901234567890")
        verify(loadNotesObserver).onChanged("123456789012345678901234")
    }
}