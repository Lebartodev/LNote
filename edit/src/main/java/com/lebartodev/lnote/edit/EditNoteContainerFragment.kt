package com.lebartodev.lnote.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.edit.databinding.FragmentEditNoteContainerBinding
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.edit.di.EditNoteComponent

class EditNoteContainerFragment : BaseFragment() {
    private val binding by viewBinding(FragmentEditNoteContainerBinding::inflate)
    private var component: EditNoteComponent? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        component = DaggerEditNoteComponent.builder()
            .appComponent((context.applicationContext as AppComponentProvider)
                .provideAppComponent())
            .context(context)
            .build().also { it.inject(this) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        component = null
    }

    companion object {
        private const val EXTRA_NOTE_DATA = "EXTRA_NOTE_DATA"

        fun initMe(noteData: NoteData = NoteData()): EditNoteContainerFragment {
            val fragment = EditNoteContainerFragment()
            fragment.arguments = Bundle()
                .apply { putParcelable(EXTRA_NOTE_DATA, noteData) }
            return fragment
        }
    }
}