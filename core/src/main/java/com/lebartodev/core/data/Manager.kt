package com.lebartodev.core.data

import com.lebartodev.core.data.NoteData
import com.lebartodev.core.db.entity.Note
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface Manager {
    interface Settings {
        fun setBottomPanelEnabled(value: Boolean)
        fun bottomPanelEnabled(): Observable<Boolean>
    }

    interface CurrentNote {
        fun currentNote(): Flow<NoteData>
        fun setState(reducer: NoteData.() -> NoteData)
        fun clear()
    }
}