package com.lebartodev.core.utils

import io.reactivex.Scheduler

interface SchedulersFacade {
    fun io(): Scheduler
    fun computation(): Scheduler
    fun ui(): Scheduler
}