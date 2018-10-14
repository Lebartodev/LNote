package com.lebartodev.lnote.notes

interface Notes {
    interface View {
        fun onNotesLoaded(notes: List<String>)
        fun onLoadError(throwable: Throwable)
    }

    interface Presenter {
        fun loadNotes()
    }
}