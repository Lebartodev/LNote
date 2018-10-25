package com.lebartodev.lnote.utils

import android.util.Log

fun Any.debug(text: String?) {
    Log.d(this.javaClass.name, text)
}

fun Any.error(text: String?, throwable: Throwable?) {
    Log.e(this.javaClass.name, text, throwable)
}