package com.lebartodev.lnote.utils.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*


class SelectDateFragment(private val listener: DatePickerDialog.OnDateSetListener, private val calendar: Calendar) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val yy: Int = calendar.get(Calendar.YEAR)
        val mm: Int = calendar.get(Calendar.MONTH)
        val dd: Int = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(context ?: throw NullPointerException(), listener, yy, mm, dd)
    }
}