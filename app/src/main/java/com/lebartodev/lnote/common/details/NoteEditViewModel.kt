package com.lebartodev.lnote.common.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import java.util.*

@DebugOpenClass
class NoteEditViewModel constructor(private val notesRepository: NotesRepository,
                                    private val schedulersFacade: SchedulersFacade) : ViewModel() {
    private var saveNoteDisposable = Disposables.empty()
    private var detailsDisposable = Disposables.empty()
    private val saveResultLiveData: MutableLiveData<ViewModelObject<Long>> = MutableLiveData()
    private val selectedDate = MutableLiveData<Long?>()
    private val saveNoteStateLiveData = SingleLiveEvent<Boolean>()
    private val deleteNoteStateLiveData = MutableLiveData<Boolean?>()

    private val currentNoteLiveData = MutableLiveData<NoteData>().apply { value = NoteData() }
    private var tempNote = NoteData()

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun saveNoteState(): LiveData<Boolean?> = saveNoteStateLiveData

    fun sdeleteNoteState(): LiveData<Boolean?> = deleteNoteStateLiveData

    fun selectedDate(): LiveData<Long?> = selectedDate

    fun saveResult(): LiveData<ViewModelObject<Long>> = saveResultLiveData

    fun setDescription(text: String) {
        val note = currentNoteLiveData.value ?: NoteData()
        note.text = text
        currentNoteLiveData.value = note
    }

    fun setTitle(title: String) {
        val note = currentNoteLiveData.value ?: NoteData()
        note.title = title
        currentNoteLiveData.value = note
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
                .apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
        val note = currentNoteLiveData.value ?: NoteData()
        note.date = calendar.timeInMillis
        currentNoteLiveData.value = note
    }

    fun clearDate() {
        val note = currentNoteLiveData.value ?: NoteData()
        note.date = null
        currentNoteLiveData.value = note
    }

    fun saveNote() {
        val note = currentNoteLiveData.value
        val title = if (note?.title.isNullOrEmpty())
            getFormattedHint(note?.text ?: "")
        else
            note?.title

        saveNoteDisposable.dispose()
        saveNoteDisposable = notesRepository.createNote(title, note?.text, selectedDate.value)
                .map { ViewModelObject.success(it) }
                .onErrorReturn { ViewModelObject.error(it, null) }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doFinally {
                    clearCurrentNote()
                    deleteTempNote()
                }
                .subscribe(Consumer { saveResultLiveData.postValue(it) }, Functions.emptyConsumer())
    }

    fun clearCurrentNote() {
        tempNote = currentNoteLiveData.value?.copy() ?: NoteData()
        currentNoteLiveData.value = NoteData()
    }

    fun undoClearCurrentNote() {
        currentNoteLiveData.value = tempNote.copy()
        tempNote = NoteData()
    }

    fun deleteTempNote() {
        tempNote = NoteData()
    }

    fun getFormattedHint(text: String): String {
        return if (text.length > MAX_TITLE_CHARACTERS) {
            text.substring(0, MAX_TITLE_CHARACTERS)
        } else {
            text
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveNoteDisposable.dispose()
        detailsDisposable.dispose()
    }

    companion object {
        private const val MAX_TITLE_CHARACTERS = 24
    }

    data class NoteData(var title: String? = null,
                        var date: Long? = null,
                        var text: String? = null)
}