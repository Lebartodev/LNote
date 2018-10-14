package com.lebartodev.lnote.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BasePresenter<V> protected constructor(protected var view: V) {

    private val disposables = CompositeDisposable()

    fun onCreate() {}

    fun onStop() {
        disposables.clear()
    }

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
}