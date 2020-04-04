package com.lebartodev.lnote.repository

import com.lebartodev.lnote.data.entity.Note
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

interface Repository {
    interface Notes {
        fun getNotes(): Flowable<List<Note>>
        fun getNote(id: Long): Flowable<Note>
        fun createNote(title: String?, text: String?, date: Long?): Single<Long>
        fun deleteNote(id: Long): Completable
        fun editNote(id: Long, title: String?, text: String?, date: Long?): Completable
        fun restoreNote(id: Long?, title: String?, text: String?, date: Long?, createdDate: Long?): Single<Long>
    }

    interface Settings {
        fun setBottomPanelEnabled(value: Boolean)
        fun bottomPanelEnabled(): Observable<Boolean>
    }
}