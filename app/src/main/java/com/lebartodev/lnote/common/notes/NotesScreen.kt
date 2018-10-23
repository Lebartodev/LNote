package com.lebartodev.lnote.common.notes

import androidx.lifecycle.LiveData
import com.lebartodev.lnote.data.entity.Note

interface NotesScreen {
    interface View {
        fun onNotesLoaded(notes: List<Note>)
        fun onLoadError(throwable: Throwable)
    }

    interface ViewModel {
        fun loadNotes(): LiveData<List<Note>>
    }
}