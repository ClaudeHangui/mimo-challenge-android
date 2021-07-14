package com.changui.mimochallengeandroid.data.scheduler

import io.reactivex.Scheduler


interface RxSchedulers {
    val main: Scheduler
    val io: Scheduler
    val computation: Scheduler
}