package com.lebartodev.lnote.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import java.text.SimpleDateFormat
import java.util.*

class DateChip @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Chip(context, attrs, defStyleAttr) {
    private var formatter = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US)

    fun setDate(date: Long?) {
        if (date != null) {
            text = formatter.format(date)
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }
}