package com.lebartodev.lnote.data

import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrentNoteManager @Inject constructor(private val schedulersFacade: SchedulersFacade) : Manager.CurrentNote {
    companion object {
        private const val NOTE_DELETE_DELAY = 5000L
    }

    private val currentNote: BehaviorSubject<NoteData> = BehaviorSubject.createDefault(NoteData())
    private val pendingDeleteSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()
    private var deleteNoteTimerDisposable = Disposables.empty()
    private var tempNote = NoteData()

    override fun getTempNote(): NoteData? = tempNote

    override fun currentNote(): Observable<NoteData> = currentNote

    override fun pendingDelete(): Observable<Boolean> = pendingDeleteSubject


    override fun setCurrentNote(note: Note) {
        currentNote.onNext(NoteData(note.id, note.title, note.date, note.text, note.created))
        currentNote.value?.run { tempNote = copy() }
    }

    override fun setTitle(value: String) = currentNote.value?.run {
        if (title != value) {
            currentNote.onNext(apply { title = value })
        }
    }

    override fun setText(value: String) = currentNote.value?.run {
        if (text != value) {
            currentNote.onNext(apply { text = value })
        }
    }

    override fun setDate(value: Long?) = currentNote.value?.run { currentNote.onNext(apply { date = value }) }

    override fun clearCurrentNote() {
        currentNote.onNext(NoteData())
    }

    override fun clearAll() {
        pendingDeleteSubject.onNext(false)
        currentNote.onNext(NoteData())
        tempNote = NoteData()
    }

    override fun deleteCurrentNote() {
        pendingDeleteSubject.onNext(true)
        currentNote.value?.run {
            if (id == null)
                tempNote = copy()
        }
        currentNote.onNext(NoteData())
        deleteNoteTimerDisposable.dispose()
        deleteNoteTimerDisposable = Completable.timer(NOTE_DELETE_DELAY, TimeUnit.MILLISECONDS)
                .subscribeOn(schedulersFacade.io())
                .subscribe {
                    clearAll()
                    pendingDeleteSubject.onNext(false)
                }
    }

    override fun undoDeletingNote() {
        deleteNoteTimerDisposable.dispose()
        if(tempNote.id==null) {
            currentNote.onNext(tempNote.copy())
        }
        tempNote = NoteData()
        pendingDeleteSubject.onNext(false)
    }
}