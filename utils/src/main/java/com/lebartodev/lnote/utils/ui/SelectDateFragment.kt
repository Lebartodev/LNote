package com.lebartodev.lnote.utils.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*


class SelectDateFragment : DialogFragment() {
    private lateinit var calendar: Calendar
    var listener: DatePickerDialog.OnDateSetListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance().apply { arguments?.getLong(EXTRA_DATE) ?: System.currentTimeMillis() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val yy: Int = calendar.get(Calendar.YEAR)
        val mm: Int = calendar.get(Calendar.MONTH)
        val dd: Int = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), listener, yy, mm, dd)
    }

    companion object {
        private const val EXTRA_DATE = "EXTRA_DATE"
        fun initMe(calendar: Calendar): SelectDateFragment {
            val fragment = SelectDateFragment()
            val args = Bundle()
            args.putLong(EXTRA_DATE, calendar.timeInMillis)
            fragment.arguments = args
            return fragment
        }
    }
}