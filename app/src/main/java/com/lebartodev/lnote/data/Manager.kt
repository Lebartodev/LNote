package com.lebartodev.lnote.data

import com.lebartodev.core.db.entity.Note
import io.reactivex.Observable

interface Manager {
    interface Settings {
        fun setBottomPanelEnabled(value: Boolean)
        fun bottomPanelEnabled(): Observable<Boolean>
    }

    interface CurrentNote {
        fun currentNote(): Observable<NoteData>
        fun setCurrentNote(note: Note)
        fun setTitle(value: String): Unit?
        fun setText(value: String): Unit?
        fun setDate(value: Long?): Unit?
    }
}