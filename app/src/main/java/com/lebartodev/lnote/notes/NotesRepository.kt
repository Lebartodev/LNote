package com.lebartodev.lnote.notes

import io.reactivex.Single
import javax.inject.Inject

class NotesRepository {
    @Inject constructor()

    fun getNotes(): Single<List<String>> {
        return Single.just(arrayListOf("a", "b", "c"))
    }
}