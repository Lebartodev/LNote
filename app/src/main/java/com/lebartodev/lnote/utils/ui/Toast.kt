package com.lebartodev.lnote.utils.ui

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.toast(text: String?) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Fragment.toast(text: String?) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}