package com.lebartodev.lnote.utils.ui

import android.view.View
import android.view.ViewTreeObserver

fun View.onLayout(listener: (() -> Unit)) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            listener.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}