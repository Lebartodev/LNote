package com.lebartodev.lnote.common.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NoteDetailsFragment : BaseFragment() {
    private var text: String? = null
    private var title: String? = null
    private var id: Long? = null

    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var deleteButton: ImageButton
    private lateinit var dateChip: Chip

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private var noteDetailsViewModel: NoteDetailsViewModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note_details, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            text = it.getString(ARG_TEXT)
            id = it.getLong(ARG_ID)
        }
        noteDetailsViewModel = ViewModelProviders.of(this, viewModelFactory)[NoteDetailsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        deleteButton = view.findViewById(R.id.delete_button)
        dateChip = view.findViewById(R.id.date_chip)
        titleTextView.text = title
        descriptionTextView.text = text


        this.noteDetailsViewModel?.apply {
            getDetails().observe(this@NoteDetailsFragment, Observer {
                if (it != null) {
                    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
                    titleTextView.text = it.title
                    descriptionTextView.text = it.text
                    it.date?.let {
                        dateChip.text = formatter.format(Date(it))
                    }
                }
            })
        }
        id?.let {
            noteDetailsViewModel?.fetchDetails(it)
        }

    }

    override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }

    companion object {
        const val TAG = "NoteDetailsFragment"
        private const val ARG_TEXT = "ARG_TEXT"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_ID = "ARG_ID"
        @JvmStatic
        fun startMe(id: Long?, title: String?, text: String?) =
                NoteDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_ID, id ?: 0)
                        putString(ARG_TITLE, title)
                        putString(ARG_TEXT, text)
                    }
                }
    }

}
