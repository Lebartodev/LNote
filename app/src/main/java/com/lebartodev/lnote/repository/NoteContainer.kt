package com.lebartodev.lnote.repository

object NoteContainer {
    val currentNote = Note()
    val tempNote = Note()

    var isSaved = false
    var isDeleted = false

    class Note {
        var title: String? = null
        var date: Long? = null
        var text: String? = null
    }
}