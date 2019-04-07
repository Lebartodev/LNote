package com.lebartodev.lnote.common.creation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.repository.CurrentNote
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import javax.inject.Inject


class NoteFragment : BaseFragment() {
    private var text: String? = null
    private var title: String? = null
    private var hint: String? = null

    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var fullScreenButton: ImageView

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private var notesViewModel: NoteEditViewModel? = null

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            notesViewModel?.onDescriptionChanged(s?.toString())
            CurrentNote.text = s?.toString()
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            CurrentNote.title = s?.toString()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            text = it.getString(ARG_TEXT)
            hint = it.getString(ARG_HINT)
        }
        notesViewModel = ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        fullScreenButton = view.findViewById(R.id.full_screen_button)
        titleTextView.text = title
        titleTextView.hint = hint
        descriptionTextView.text = text
        descriptionTextView.addTextChangedListener(descriptionTextWatcher)
        titleTextView.addTextChangedListener(titleTextWatcher)


        fullScreenButton.setOnClickListener {
            hideKeyboard(descriptionTextView)
            fragmentManager?.popBackStack()
        }

        this.notesViewModel?.apply {
            descriptionTextLiveData.observe(this@NoteFragment, Observer {
                if (it != null && it != titleTextView.hint) {
                    if (it.isNotEmpty())
                        titleTextView.hint = it
                    else
                        titleTextView.hint = context?.getString(R.string.title_hint)
                }
            })
            selectedDateString().observe(this@NoteFragment, Observer {
                //dateText.setText(it)
            })
            getSaveResult().observe(this@NoteFragment, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        descriptionTextView.removeTextChangedListener(descriptionTextWatcher)
        titleTextView.removeTextChangedListener(titleTextWatcher)
    }

    companion object {
        private const val ARG_TEXT = "ARG_TEXT"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_HINT = "ARG_HINT"
        @JvmStatic
        fun startMe(title: String?, hint: String?, text: String?) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_HINT, hint)
                        putString(ARG_TEXT, text)
                    }
                }
    }

    override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }

}
