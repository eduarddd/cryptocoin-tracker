package com.nightlydev.cryptocointracker.extensions


import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by edu on 10-3-18.
 */
fun <T> Observable<T>.observeAndSubscribeOn(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
}

fun <T> Single<T>.observeAndSubscribeOn(): Single<T> {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.apiSubscribe(observer: Observer<in T>) {
    observeAndSubscribeOn().subscribe(observer)
}
