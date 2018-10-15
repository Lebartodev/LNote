package com.lebartodev.lnote.common.notes

import com.lebartodev.lnote.data.AppDatabase
import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Single
import javax.inject.Inject


class NotesRepository {
    @Inject
    lateinit var database: AppDatabase
    @Inject
    lateinit var schedulersFacade: SchedulersFacade

    fun getNotes(): Single<List<String>> {
        return Single.just(arrayListOf("a", "b", "c"))
    }
}