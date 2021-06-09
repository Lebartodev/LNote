package com.lebartodev.lnote.utils.di.app

import com.lebartodev.core.utils.SchedulersFacade
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SchedulersFacadeMock @Inject constructor() : SchedulersFacade {
    override fun io(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}