package com.lebartodev.lnote.data.entity

data class ViewModelObject<out T>(val status: Status, val data: T?, val error: Throwable?) {
    companion object {
        fun <T> success(data: T?): ViewModelObject<T> {
            return ViewModelObject(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable, data: T?): ViewModelObject<T> {
            return ViewModelObject(Status.ERROR, data, error)
        }

        fun <T> loading(data: T?): ViewModelObject<T> {
            return ViewModelObject(Status.LOADING, data, null)
        }
    }
}