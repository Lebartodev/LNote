package com.lebartodev.lnote.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SchedulersFacade {

    fun io(): Scheduler {
        return Schedulers.io()
    }

    fun computation(): Scheduler {
        return Schedulers.computation()
    }

    fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}