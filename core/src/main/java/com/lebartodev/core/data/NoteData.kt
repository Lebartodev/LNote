package com.lebartodev.core.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteData(var id: Long? = null,
                    var title: String? = null,
                    var date: Long? = null,
                    var text: String? = null,
                    var dateCreated: Long? = null) : Parcelable