package com.lebartodev.lnote.utils

import io.reactivex.Scheduler

interface SchedulersFacade {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}