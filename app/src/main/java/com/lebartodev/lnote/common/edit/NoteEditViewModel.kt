package com.lebartodev.lnote.common.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.lnote.base.BaseViewModel
import com.lebartodev.lnote.data.Manager
import com.lebartodev.lnote.data.NoteData
import com.lebartodev.lnote.repository.Repository
import com.lebartodev.lnote.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.exception.DeleteNoteException
import com.lebartodev.lnote.utils.exception.RestoreNoteException
import com.lebartodev.lnote.utils.extensions.formattedHint
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.functions.Functions
import java.util.*

class NoteEditViewModel constructor(private val notesRepository: Repository.Notes,
                                    settingsManager: Manager.Settings,
                                    private val schedulersFacade: SchedulersFacade) : BaseViewModel() {
    private val disposables = CompositeDisposable()

    private val saveResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val deleteResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val bottomPanelEnabledLiveData = MutableLiveData<Boolean?>()
    private val currentNoteLiveData = MutableLiveData<NoteData>(NoteData())

    private val forceEditCurrentNoteSignal = SingleLiveEvent<Boolean>()

    init {
        disposables.add(settingsManager.bottomPanelEnabled()
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { bottomPanelEnabledLiveData.value = it },
                        Functions.emptyConsumer()))
    }

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun bottomPanelEnabled(): LiveData<Boolean?> = bottomPanelEnabledLiveData

    fun saveResult(): LiveData<Boolean?> = saveResultLiveData

    fun deleteResult(): LiveData<Boolean?> = deleteResultLiveData

    fun forceEditCurrentNote(): LiveData<Boolean?> = forceEditCurrentNoteSignal

    fun loadNote(id: Long) {
        disposables.add(notesRepository.getNote(id)
                .firstOrError()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Consumer { note ->
                    currentNoteLiveData.value = NoteData(note.id, note.title, note.date, note.text, note.created)
                }, Functions.emptyConsumer()))
    }

    fun setDescription(value: String) {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { text = value }
    }

    fun setTitle(value: String) {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { title = value }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
                .apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { date = calendar.timeInMillis }
    }

    fun clearDate() {
        currentNoteLiveData.value = currentNoteLiveData.value?.apply { date = null }
    }

    fun saveNote() {
        val note = currentNoteLiveData.value
        val title = if (note?.title.isNullOrEmpty())
            (note?.text ?: "").formattedHint()
        else
            note?.title

        disposables.add(Completable
                .defer {
                    val id = note?.id
                    if (id == null) {
                        notesRepository.createNote(title, note?.text, note?.date).ignoreElement()
                    } else {
                        notesRepository.editNote(id, title, note.text, note.date)
                    }
                }
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    saveResultLiveData.value = true
                    currentNoteLiveData.value = NoteData()
                }, {
                    postError(it)
                }))
    }

    fun restoreLastNote() {
        disposables.add(notesRepository.restoreLastNote()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    if (it.created == null) {
                        currentNoteLiveData.value = NoteData(it.id, it.title, it.date, it.text, it.created)
                        forceEditCurrentNoteSignal.value = true
                    }
                }, { postError(RestoreNoteException(it)) }))
    }

    fun deleteEditedNote() {
        val note = currentNoteLiveData.value
        val title = if (note?.title.isNullOrEmpty())
            (note?.text ?: "").formattedHint()
        else
            note?.title

        disposables.add(notesRepository.deleteDraftedNote(title, note?.text, note?.date)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(Action {
                    currentNoteLiveData.value = NoteData()
                    deleteResultLiveData.value = true
                }, Functions.emptyConsumer()))
    }

    fun deleteNote(id: Long) {
        disposables.add(notesRepository.deleteNote(id)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({ deleteResultLiveData.value = true }, { postError(DeleteNoteException(it)) }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}