package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NoteContainer
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import java.util.*

@DebugOpenClass
class NoteEditViewModel constructor(var notesRepository: NotesRepository, val schedulersFacade: SchedulersFacade) : ViewModel() {
    private var saveNoteDisposable = Disposables.empty()
    private var detailsDisposable = Disposables.empty()
    private val saveResultLiveData: MutableLiveData<ViewModelObject<Long>> = MutableLiveData()
    private val selectedDate = MutableLiveData<Long?>()
    val descriptionTextLiveData = MutableLiveData<String?>()
    val noteDetailsLiveData = MutableLiveData<Note?>()

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        selectedDate.postValue(calendar.timeInMillis)
        NoteContainer.currentNote.date = calendar.timeInMillis
    }

    fun clearDate() {
        selectedDate.postValue(null)
        NoteContainer.currentNote.date = null
    }

    fun selectedDate(): LiveData<Long?> = selectedDate

    fun saveResult(): LiveData<ViewModelObject<Long>> = saveResultLiveData

    fun descriptionTextLiveData(): LiveData<String?> = descriptionTextLiveData

    fun saveNote(title: String?, text: String?) {
        saveNoteDisposable.dispose()
        saveNoteDisposable = notesRepository.createNote(title, text, selectedDate.value)
                .map { ViewModelObject.success(it) }
                .onErrorReturn { ViewModelObject.error(it, null) }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { saveResultLiveData.postValue(it) }, Functions.emptyConsumer())
    }

    fun onDescriptionChanged(text: String?) {
        if (text != null && text.length > MAX_TITLE_CHARACTERS)
            descriptionTextLiveData.postValue(text.substring(0, MAX_TITLE_CHARACTERS))
        else {
            descriptionTextLiveData.postValue(text)
        }
    }

    fun getDetails(): LiveData<Note?> = noteDetailsLiveData

    fun fetchDetails(id: Long) {
        saveNoteDisposable.dispose()
        detailsDisposable = notesRepository.getNote(id)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { noteDetailsLiveData.postValue(it) }, Functions.emptyConsumer())
    }

    override fun onCleared() {
        super.onCleared()
        saveNoteDisposable.dispose()
        detailsDisposable.dispose()
    }

    companion object {
        private const val MAX_TITLE_CHARACTERS = 24
    }
}