package com.lebartodev.lnote.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.di.app.AppComponent

class ShowNoteFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }
    override fun setupComponent(component: AppComponent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}