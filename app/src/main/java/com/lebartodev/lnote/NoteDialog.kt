package com.lebartodev.lnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class NoteDialog : DialogFragment() {
    private var title: String? = null
    private var description: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            description = it.getString(ARG_DESCRIPTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.note_dialog_background)
        return inflater.inflate(R.layout.dialog_note, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleTextView = view.findViewById<TextView>(R.id.note_title)
        val descriptionTextView = view.findViewById<TextView>(R.id.note_description)
        titleTextView.text = title
        descriptionTextView.text = description
    }

    companion object {
        private val ARG_TITLE = "ARG_TITLE"
        private val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        fun startMe(id: Long?, title: String?, description: String): NoteDialog {
            val dialog = NoteDialog()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            dialog.arguments = args
            return dialog
        }
    }
}