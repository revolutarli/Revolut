package com.challenge.revolut.shared

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulerProvider{
    fun io(): Scheduler

    fun computation(): Scheduler

    fun ui(): Scheduler
}

class SchedulerProviderImpl: SchedulerProvider{
    override fun io() = Schedulers.io()

    override fun computation() = Schedulers.computation()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
}