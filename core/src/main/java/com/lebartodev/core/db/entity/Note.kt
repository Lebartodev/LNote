package com.lebartodev.core.db.entity

import androidx.room.Relation

class Note(id: Long? = null,
           title: String?,
           date: Long?,
           created: Long?,
           text: String,
           deletedDate: Long? = null) : NoteEntity(id, title, date, created, text, deletedDate) {
    @Relation(parentColumn = "id", entityColumn = "noteId")
    var photos: List<Photo> = arrayListOf()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}