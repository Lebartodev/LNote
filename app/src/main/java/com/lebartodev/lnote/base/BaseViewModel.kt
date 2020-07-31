package com.lebartodev.lnote.base

import androidx.lifecycle.*

abstract class BaseViewModel : ViewModel() {
    private val error = MutableLiveData<Throwable?>()
    fun error(): LiveData<Throwable?> = error
    fun postError(throwable: Throwable) = error.postValue(throwable)
    fun observeError(lifecycleOwner: LifecycleOwner, tag: String) {
        error.observe(lifecycleOwner, Observer {


        })
    }
}