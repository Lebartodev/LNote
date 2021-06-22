package com.lebartodev.lnote.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lebartodev.core.base.BaseViewModel
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.data.repository.Repository
import com.lebartodev.core.utils.SchedulersFacade
import com.lebartodev.lnote.utils.SingleLiveEvent
import com.lebartodev.lnote.utils.extensions.formattedHint
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import java.util.*

class NoteEditViewModel constructor(
    private val notesRepository: Repository.Notes,
    private val schedulersFacade: SchedulersFacade
) : BaseViewModel() {
    private val disposables = CompositeDisposable()

    private val saveResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val deleteResultLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val currentNoteLiveData = MutableLiveData(NoteData())

    fun currentNote(): LiveData<NoteData> = currentNoteLiveData

    fun saveResult(): LiveData<Boolean?> = saveResultLiveData

    fun deleteResult(): LiveData<Boolean?> = deleteResultLiveData

    fun loadNote(id: Long) {
        disposables.add(
            notesRepository.getNote(id)
                .firstOrError()
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({ note ->
                    this.currentNoteLiveData.value = NoteData(note.id, note.title, note.date,
                        note.text, note.created)
                }, Functions.emptyConsumer())
        )
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
            })
        )
    }

    fun deleteEditedNote() {
        val note = currentNoteLiveData.value
        val title = if (note?.title.isNullOrEmpty())
            (note?.text ?: "").formattedHint()
        else
            note?.title

        disposables.add(
            notesRepository.deleteDraftedNote(title, note?.text, note?.date)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe({
                    currentNoteLiveData.value = NoteData()
                    deleteResultLiveData.value = true
                }, Functions.emptyConsumer())
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}