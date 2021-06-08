package com.lebartodev.core.base

import androidx.lifecycle.*

abstract class BaseViewModel : ViewModel() {
    private val error = MutableLiveData<Throwable?>()
    fun error(): LiveData<Throwable?> = error
    fun postError(throwable: Throwable) = error.postValue(throwable)
}