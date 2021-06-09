package com.lebartodev.core.data.repository

import com.lebartodev.core.db.entity.Note
import io.reactivex.*

interface Repository {
    interface Notes {
        fun getNotes(): Flowable<List<Note>>
        fun getNote(id: Long): Flowable<Note>
        fun createNote(title: String?, text: String?, date: Long?): Single<Long>
        fun deleteDraftedNote(title: String?, text: String?, date: Long?): Completable
        fun deleteNote(id: Long): Completable
        fun editNote(id: Long, title: String?, text: String?, date: Long?): Completable
        fun restoreLastNote(): Maybe<Note>
        fun getArchive(): Flowable<List<Note>>
    }

    interface Settings {
        fun setBottomPanelEnabled(value: Boolean)
        fun bottomPanelEnabled(): Observable<Boolean>
    }
}