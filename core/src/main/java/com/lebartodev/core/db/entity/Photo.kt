package com.lebartodev.core.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [
    ForeignKey(entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE)
])
data class Photo(
        @PrimaryKey val id: String,
        val path: String,
        val date: Long,
        var noteId: Long = 0L
) : Parcelable