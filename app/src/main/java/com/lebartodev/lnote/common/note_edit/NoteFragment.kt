package com.lebartodev.lnote.common.note_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.creation.NoteEditViewModel
import javax.inject.Inject


class NoteFragment : Fragment() {
    private var text: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            text = it.getString(ARG_TEXT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }


    companion object {
        private const val ARG_TEXT = "ARG_TEXT"
        private const val ARG_TITLE = "ARG_TITLE"
        @JvmStatic
        fun startMe(title: String?, text: String?) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_TEXT, text)
                    }
                }
    }
}
