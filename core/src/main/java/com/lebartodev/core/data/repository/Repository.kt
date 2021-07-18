package com.lebartodev.core.data.repository

import com.lebartodev.core.db.entity.Note
import com.lebartodev.core.db.entity.Photo
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface Repository {
    interface Notes {
        fun getNotes(): Flow<List<Note>>
        fun getNote(id: Long): Flow<Note>
        suspend fun createNote(title: String?, text: String?, date: Long?,
                               photos: List<Photo>): Long

        suspend fun deleteDraftedNote(title: String?, text: String?, date: Long?)
        suspend fun deleteNote(id: Long)
        suspend fun editNote(id: Long, title: String?, text: String?, date: Long?)
        suspend fun restoreLastNote(): Note
        fun getArchive(): Flow<List<Note>>
        suspend fun completleDeleteNote(id: Long)
        suspend fun restoreNote(id: Long)
    }

    interface Settings {
        fun setBottomPanelEnabled(value: Boolean)
        fun bottomPanelEnabled(): Observable<Boolean>
    }
}