package com.lebartodev.lnote.common.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.data.NoteData
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.repository.Repository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.extensions.formattedHint
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import java.util.*

class NoteEditViewModel constructor(private val notesRepository: Repository.Notes,
                                    settingsManager: Manager.Settings,
                                    private val schedulersFacade: SchedulersFacade,
                                    private val currentNoteManager: Manager.CurrentNote) : ViewModel() {
    private var saveNoteDisposable = Disposables.empty()
    private var deleteNoteDisposable = Disposables.empty()
    private var detailsDisposable = Disposables.empty()
    private var bottomPanelEnabledDisposable = Disposables.empty()
    private var noteDisposable = Disposables.empty()
    private var pendingDeleteDisposable = Disposables.empty()

    private val saveResultLiveData: SingleLiveEvent<ViewModelObject<Long>> = SingleLiveEvent()
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean?>()
    private val pendingDeleteLiveData = MutableLiveData<Boolean?>()

    private val currentNoteLiveData = MutableLiveData<NoteData>()

    init {
        bottomPanelEnabledDisposable = settingsManager.bottomPanelEnabled()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { bottomPanelEnabledLiveData.value = it },
                        Functions.emptyConsumer())

        pendingDeleteDisposable = currentNoteManager.pendingDelete()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { pendingDeleteLiveData.value = it },
                        Functions.emptyConsumer())

        noteDisposable = currentNoteManager.currentNote()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer {
                    currentNoteLiveData.value = it
                }, Functions.emptyConsumer())
    }

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun pendingDelete(): LiveData<Boolean?> = pendingDeleteLiveData

    fun bottomPanelEnabled(): LiveData<Boolean?> = bottomPanelEnabledLiveData

    fun saveResult(): LiveData<ViewModelObject<Long>?> = saveResultLiveData

    fun loadNote(id: Long) {
        detailsDisposable.dispose()
        detailsDisposable = notesRepository.getNote(id)
                .firstOrError()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer {
                    currentNoteManager.setCurrentNote(it)
                }, Functions.emptyConsumer())
    }

    fun setDescription(text: String) {
        currentNoteManager.setText(text)
    }

    fun setTitle(title: String) {
        currentNoteManager.setTitle(title)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
                .apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
        currentNoteManager.setDate(calendar.timeInMillis)
    }

    fun clearDate() {
        currentNoteManager.setDate(null)
    }

    fun saveNote() {
        val note = currentNoteLiveData.value
        val title = if (note?.title.isNullOrEmpty())
            (note?.text ?: "").formattedHint()
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
                    currentNoteManager.clearAll()
                    saveResultLiveData.value = it
                }, Functions.emptyConsumer())
    }

    fun deleteCurrentNote() {
        currentNoteManager.deleteCurrentNote()
    }

    fun deleteEditedNote() {
        currentNoteLiveData.value?.id?.run {
            deleteNoteDisposable.dispose()
            deleteNoteDisposable = notesRepository.deleteNote(this)
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .subscribe(Action {
                        currentNoteManager.deleteCurrentNote()
                    }, Functions.emptyConsumer())
        }
    }

    fun undoDeleteCurrentNote() {
        if (currentNoteManager.getTempNote()?.id != null) {
            currentNoteManager.getTempNote()?.run {
                notesRepository.restoreNote(this.id, this.title, this.text, this.date, this.dateCreated)
                        .subscribeOn(schedulersFacade.io())
                        .observeOn(schedulersFacade.ui())
                        .subscribe(Consumer {
                            currentNoteManager.undoDeletingNote()
                        }, Functions.emptyConsumer())
            }
        } else {
            currentNoteManager.undoDeletingNote()
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveNoteDisposable.dispose()
        detailsDisposable.dispose()
        deleteNoteDisposable.dispose()
        bottomPanelEnabledDisposable.dispose()
        noteDisposable.dispose()
        pendingDeleteDisposable.dispose()
    }

    fun clearCurrentNote() {
        currentNoteManager.clearAll()
    }
}