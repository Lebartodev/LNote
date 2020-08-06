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
    private val currentNote: BehaviorSubject<NoteData> = BehaviorSubject.createDefault(NoteData())
    private var tempNote = NoteData()

    override fun currentNote(): Observable<NoteData> = currentNote

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
}