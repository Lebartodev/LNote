package com.lebartodev.lnote.common.notes

import com.lebartodev.lnote.data.AppDatabase
import io.reactivex.Single
import javax.inject.Inject


class NotesRepository @Inject constructor() {
    @Inject
    lateinit var database: AppDatabase

    fun getNotes(): Single<List<String>> {
        return Single.just(arrayListOf("a", "b", "c"))
    }
}