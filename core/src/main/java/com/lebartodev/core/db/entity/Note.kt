package com.lebartodev.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(@PrimaryKey(autoGenerate = true) val id: Long? = null,
                var title: String?,
                var date: Long?,
                val created: Long?,
                var text: String,
                val deletedDate: Long? = null)