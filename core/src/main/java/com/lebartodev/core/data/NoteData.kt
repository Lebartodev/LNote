package com.lebartodev.core.data

import android.os.Parcelable
import com.lebartodev.core.db.entity.Photo
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteData(
    var id: Long? = null,
    var title: String? = null,
    var date: Long? = null,
    var text: String? = null,
    var dateCreated: Long? = null,
    var photos: List<Photo> = arrayListOf()
) : Parcelable

fun NoteData.isEmpty(): Boolean {
    return title.isNullOrEmpty() || text.isNullOrEmpty() || photos.isNullOrEmpty()
}