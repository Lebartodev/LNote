package com.lebartodev.lnote.utils.mocks

import com.lebartodev.lnote.utils.SchedulersFacade
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SchedulersFacadeMock @Inject constructor() : SchedulersFacade {
    override fun io(): Scheduler = Schedulers.trampoline()

    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}