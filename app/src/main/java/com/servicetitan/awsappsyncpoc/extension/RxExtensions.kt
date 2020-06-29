package com.servicetitan.awsappsyncpoc.extension

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.scheduleIOToMainThread() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.scheduleIOToMainThread() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.scheduleIOToMainThread() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.scheduleIOToMainThread() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun Completable.scheduleIOToMainThread() =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
