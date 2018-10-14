package com.lebartodev.lnote.common.notes

interface NotesScreen {
    interface View {
        fun onNotesLoaded(notes: List<String>)
        fun onLoadError(throwable: Throwable)
    }

    interface ViewModel {
        fun loadNotes()
    }
}