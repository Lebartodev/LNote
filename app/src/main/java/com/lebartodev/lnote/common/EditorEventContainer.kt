package com.lebartodev.lnote.common

interface EditorEventContainer {
    fun popEditorEvent(): EditorEvent?
    fun deleteNote()
    fun saveNote()
}


enum class EditorEvent {
    SAVE, DELETE
}