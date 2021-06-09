package com.lebartodev.lnote.common.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.lebartodev.lnote.edit.NoteEditViewModel
import com.lebartodev.core.data.Manager
import com.lebartodev.lnote.utils.ViewModelObject
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.di.app.DaggerAppComponentMock
import com.lebartodev.lnote.utils.di.notes.DaggerNotesComponentMock
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import javax.inject.Inject

@RunWith(JUnit4::class)
class NoteEditViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @Inject
    lateinit var schedulersFacade: SchedulersFacade
    @Inject
    lateinit var notesRepository: Repository.Notes
    @Inject
    lateinit var settingsManager: Manager.Settings
    @Inject
    lateinit var currentNoteManager: Manager.CurrentNote

    lateinit var editViewModel: com.lebartodev.lnote.edit.NoteEditViewModel

    private val calendarObserver: Observer<Long?> = mock()

    @Before
    fun setUp() {
        val comp = DaggerAppComponentMock.builder()
                .applicationContext(mock())
                .build()
        DaggerNotesComponentMock.builder().appComponent(comp).context(mock()).build().inject(this)
        editViewModel = com.lebartodev.lnote.edit.NoteEditViewModel(notesRepository, settingsManager, schedulersFacade, currentNoteManager)

        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Single.just(1L))
    }

    @Test
    fun setDate() {
        val dateLiveData = Transformations.map(editViewModel.currentNote()) { note -> note?.date }
        dateLiveData.observeForever(calendarObserver)
        editViewModel.setDate(1996, 5, 5)
        assert(dateLiveData.value != null)
        val date = Calendar.getInstance()
        date.timeInMillis = dateLiveData.value ?: 0

        assertThat(date.get(Calendar.YEAR), CoreMatchers.equalTo(1996))
        assertThat(date.get(Calendar.MONTH), CoreMatchers.equalTo(5))
        assertThat(date.get(Calendar.DAY_OF_MONTH), CoreMatchers.equalTo(5))
    }

    @Test
    fun clearDate() {
        editViewModel.setDate(1, 1, 1)
        assertNotNull(editViewModel.currentNote().value?.date)
        editViewModel.clearDate()
        assertNull(editViewModel.currentNote().value?.date)
    }

    @Test
    fun saveNote() {
        val saveNoteObserver: Observer<ViewModelObject<Long>?> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.setTitle("title")
        editViewModel.setDescription("text")
        editViewModel.saveNote()
        verify(notesRepository).createNote("title", "text", null)
        verify(saveNoteObserver).onChanged(ViewModelObject.success(1L))
    }

    @Test
    fun saveNoteWithDate() {
        val saveNoteObserver: Observer<ViewModelObject<Long>?> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.setDate(1996, 4, 5)
        editViewModel.setTitle("title")
        editViewModel.setDescription("text")

        val date = editViewModel.currentNote().value?.date

        editViewModel.saveNote()
        verify(notesRepository).createNote("title", "text", date)
        verify(saveNoteObserver).onChanged(ViewModelObject.success(1L))
    }

    @Test
    fun saveNoteWithError() {
        val exception = NullPointerException()
        whenever(notesRepository.createNote(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
                Single.error(exception))

        val saveNoteObserver: Observer<ViewModelObject<Long>?> = mock()
        editViewModel.saveResult().observeForever(saveNoteObserver)
        editViewModel.setDate(1996, 4, 5)
        editViewModel.setTitle("title")
        val date = editViewModel.currentNote().value?.date
        editViewModel.saveNote()
        verify(notesRepository).createNote("title", null, date)
        verify(saveNoteObserver).onChanged(ViewModelObject.error(exception, null))
    }


    @Test
    fun onDescriptionChanged() {
        val descriptionLiveData = Transformations.map(editViewModel.currentNote()) { note -> note?.text }


        val loadNotesObserver: Observer<String?> = mock()
        descriptionLiveData.observeForever(loadNotesObserver)

        editViewModel.setDescription("1234567890")
        verify(loadNotesObserver).onChanged("1234567890")
    }
}