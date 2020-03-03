package com.lebartodev.lnote.common.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.NotesRepository
import com.lebartodev.lnote.utils.DebugOpenClass
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import java.util.*

@DebugOpenClass
class NoteEditViewModel constructor(private val notesRepository: NotesRepository,
                                    private val schedulersFacade: SchedulersFacade) : ViewModel() {
    private var saveNoteDisposable = Disposables.empty()
    private var deleteNoteDisposable = Disposables.empty()
    private var detailsDisposable = Disposables.empty()
    private val saveResultLiveData: SingleLiveEvent<ViewModelObject<Long>> = SingleLiveEvent()
    private val showNoteDeletedLiveData = MutableLiveData<Boolean?>()
    private val moreOpenLiveData = MutableLiveData<Boolean>().apply { value = false }
    private val dateDialogLiveData = MutableLiveData<Calendar>()
    private val bottomSheetOpenLiveData = MutableLiveData<Boolean>().apply { value = false }


    private val currentNoteLiveData = MutableLiveData<NoteData>().apply { value = NoteData() }
    private var tempNote = NoteData()

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun showNoteDeleted(): LiveData<Boolean?> = showNoteDeletedLiveData

    fun isMoreOpen(): LiveData<Boolean> = moreOpenLiveData

    fun saveResult(): LiveData<ViewModelObject<Long>?> = saveResultLiveData

    fun dateDialog(): LiveData<Calendar?> = dateDialogLiveData

    fun bottomSheetOpen(): LiveData<Boolean> = bottomSheetOpenLiveData

    fun loadNote(id: Long) {
        detailsDisposable.dispose()
        detailsDisposable = notesRepository.getNote(id)
                .firstOrError()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer {
                    currentNoteLiveData.value = NoteData(it.id, it.title, it.date, it.text, it.created)
                }, Functions.emptyConsumer())
    }

    fun setDescription(text: String) {
        val note = currentNoteLiveData.value ?: NoteData()
        if (note.text != text) {
            note.text = text
            currentNoteLiveData.value = note
        }
    }

    fun setTitle(title: String) {
        val note = currentNoteLiveData.value ?: NoteData()
        if (note.title != title) {
            note.title = title
            currentNoteLiveData.value = note
        }
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
        saveNoteDisposable = Completable
                .defer {
                    val id = note?.id
                    if (id == null) {
                        notesRepository.createNote(title, note?.text, note?.date).ignoreElement()
                    } else {
                        notesRepository.editNote(id, title, note.text, note.date)
                    }
                }
                .toSingle { 1L }
                .map { ViewModelObject.success(it) }
                .onErrorReturn { ViewModelObject.error(it, null) }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer {
                    currentNoteLiveData.value = NoteData()
                    tempNote = NoteData()
                    saveResultLiveData.value = it
                    closeEditFlow()
                }, Functions.emptyConsumer())
    }

    fun clearCurrentNote() {
        val id = currentNoteLiveData.value?.id
        if (id == null) {
            closeEditFlow()
            tempNote = currentNoteLiveData.value?.copy() ?: NoteData()
            showNoteDeletedLiveData.value = true
        } else {
            detailsDisposable.dispose()
            detailsDisposable = notesRepository.getNote(id)
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .subscribe(Consumer {
                        tempNote = NoteData(it.id, it.title, it.date, it.text, it.created)
                        closeEditFlow()
                        showNoteDeletedLiveData.value = true
                    }, Functions.emptyConsumer())
        }
    }

    fun deleteEditedNote() {
        currentNoteLiveData.value?.id?.run {
            deleteNoteDisposable.dispose()
            deleteNoteDisposable = notesRepository.deleteNote(this)
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .subscribe(Action {
                        closeEditFlow()
                        tempNote = currentNoteLiveData.value?.copy() ?: NoteData()
                        showNoteDeletedLiveData.value = true
                    }, Functions.emptyConsumer())
        }
    }

    fun onCurrentNoteCleared() {
        showNoteDeletedLiveData.value = false
        currentNoteLiveData.value = NoteData()
        tempNote = NoteData()
    }

    fun undoClearCurrentNote() {
        if (tempNote.id != null) {
            tempNote.run {
                notesRepository.restoreNote(this.id, this.title, this.text, this.date, this.dateCreated)
                        .subscribeOn(schedulersFacade.io())
                        .observeOn(schedulersFacade.ui())
                        .subscribe(Consumer {
                            tempNote = NoteData()
                            showNoteDeletedLiveData.value = false
                        }, Functions.emptyConsumer())
            }
        } else {
            currentNoteLiveData.value = tempNote.copy()
            tempNote = NoteData()
            bottomSheetOpenLiveData.value = true
            showNoteDeletedLiveData.value = false
        }
    }

    private fun closeEditFlow() {
        moreOpenLiveData.value = false
        bottomSheetOpenLiveData.value = false
    }

    fun toggleMore() {
        moreOpenLiveData.value = !(moreOpenLiveData.value ?: false)
    }

    fun getFormattedHint(text: String): String {
        val separateIndex = text.indexOf("\n")
        var firstLine: String = ""
        if (separateIndex != -1) {
            firstLine = text.substring(0, separateIndex)
        } else
            firstLine = text
        return if (firstLine.length > MAX_TITLE_CHARACTERS) {
            firstLine.substring(0, MAX_TITLE_CHARACTERS)
        } else {
            firstLine
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveNoteDisposable.dispose()
        detailsDisposable.dispose()
        deleteNoteDisposable.dispose()
    }

    fun resetCurrentNote() {
        currentNoteLiveData.value = NoteData()
    }

    fun openDateDialog() {
        dateDialogLiveData.value = Calendar.getInstance().apply { timeInMillis = currentNote().value?.date ?: System.currentTimeMillis() }
    }

    fun closeDateDialog() {
        dateDialogLiveData.value = null
    }

    fun toggleBottomSheet() {
        bottomSheetOpenLiveData.value = !(bottomSheetOpenLiveData.value ?: false)
    }

    companion object {
        private const val MAX_TITLE_CHARACTERS = 24
    }

    data class NoteData(var id: Long? = null,
                        var title: String? = null,
                        var date: Long? = null,
                        var text: String? = null,
                        var dateCreated: Long? = null)
}