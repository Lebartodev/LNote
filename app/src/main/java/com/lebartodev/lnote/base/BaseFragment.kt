package com.lebartodev.lnote.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.component.AppComponent


abstract class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            setupComponent(LNoteApplication[it].component())
        }

    }

    protected abstract fun setupComponent(component: AppComponent)

    fun hideKeyboard(additionalEditText: View? = null) {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (additionalEditText != null) {
            view = additionalEditText
        }
        if (view == null) {
            view = View(context)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}