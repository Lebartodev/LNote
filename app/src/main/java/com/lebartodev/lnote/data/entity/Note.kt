package com.lebartodev.lnote.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(@PrimaryKey val id: Long,
                val title: String,
                val text: String)