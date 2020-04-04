package com.lebartodev.lnote.data

data class NoteData(var id: Long? = null,
                    var title: String? = null,
                    var date: Long? = null,
                    var text: String? = null,
                    var dateCreated: Long? = null)