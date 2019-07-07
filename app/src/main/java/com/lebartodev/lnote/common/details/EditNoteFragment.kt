package com.lebartodev.lnote.common.details

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.component.AppComponent
import com.lebartodev.lnote.di.module.NotesModule
import com.lebartodev.lnote.repository.NoteContainer
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditNoteFragment : BaseFragment() {
    private var text: String? = null
    private var title: String? = null
    private var hint: String? = null
    private var date: Long = EMPTY_DATE

    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var fullScreenButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveNoteButton: MaterialButton
    private lateinit var calendarButton: ImageButton
    private lateinit var dateChip: Chip

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private var notesViewModel: NoteEditViewModel? = null
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        notesViewModel?.setDate(y, m, d)
    }

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            notesViewModel?.onDescriptionChanged(s?.toString())
            NoteContainer.currentNote.text = s?.toString()
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            NoteContainer.currentNote.title = s?.toString()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            text = it.getString(ARG_TEXT)
            hint = it.getString(ARG_HINT)
            date = it.getLong(ARG_DATE, EMPTY_DATE)
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
        saveNoteButton = view.findViewById(R.id.save_button)
        deleteButton = view.findViewById(R.id.delete_button)
        dateChip = view.findViewById(R.id.date_chip)
        calendarButton = view.findViewById(R.id.calendar_button)
        titleTextView.text = title
        titleTextView.hint = hint
        descriptionTextView.text = text

        if (date != EMPTY_DATE) {
            dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US).format(Date(date))
            dateChip.visibility = View.VISIBLE
        } else {
            dateChip.visibility = View.GONE
        }
        descriptionTextView.addTextChangedListener(descriptionTextWatcher)
        titleTextView.addTextChangedListener(titleTextWatcher)


        fullScreenButton.setOnClickListener {
            hideKeyboardListener(titleTextView) {
                fragmentManager?.popBackStack()
            }
        }
        deleteButton.setOnClickListener {
            hideKeyboardListener(titleTextView) {
                titleTextView.clearFocus()
                descriptionTextView.clearFocus()
                NoteContainer.tempNote.text = NoteContainer.currentNote.text
                NoteContainer.tempNote.title = NoteContainer.currentNote.title
                NoteContainer.tempNote.date = NoteContainer.currentNote.date

                NoteContainer.currentNote.text = null
                NoteContainer.currentNote.title = null
                NoteContainer.currentNote.date = null

                NoteContainer.isDeleted = true

                fragmentManager?.popBackStack()
            }
        }
        saveNoteButton.setOnClickListener {
            hideKeyboardListener(titleTextView) {
                val savedTitle = if (titleTextView.text.isNullOrEmpty()) titleTextView.hint.toString() else titleTextView.text.toString()
                notesViewModel?.saveNote(title = savedTitle, text = descriptionTextView.text.toString())
                titleTextView.clearFocus()
                descriptionTextView.clearFocus()
                NoteContainer.currentNote.text = null
                NoteContainer.currentNote.title = null
                NoteContainer.currentNote.date = null
            }
        }
        calendarButton.setOnClickListener {
            openCalendarDialog(if (date == EMPTY_DATE) null else date)
        }
        dateChip.setOnClickListener {
            openCalendarDialog(if (date == EMPTY_DATE) null else date)
        }
        dateChip.setOnCloseIconClickListener {
            notesViewModel?.clearDate()
        }

        this.notesViewModel?.apply {
            descriptionTextLiveData().observe(this@EditNoteFragment, Observer {
                if (it != null && it != titleTextView.hint) {
                    if (it.isNotEmpty())
                        titleTextView.hint = it
                    else
                        titleTextView.hint = context?.getString(R.string.title_hint)
                }
            })
            selectedDate().observe(this@EditNoteFragment, Observer { time ->
                if (time != null) {
                    dateChip.visibility = View.VISIBLE
                    dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US).format(Date(time))
                } else {
                    dateChip.visibility = View.GONE
                }
                calendarButton.setOnClickListener {
                    openCalendarDialog(time)
                }
                dateChip.setOnClickListener {
                    openCalendarDialog(time)
                }
            })
            saveResult().observe(this@EditNoteFragment, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                } else if (obj.status == Status.SUCCESS) {
                    NoteContainer.isSaved = true
                    fragmentManager?.popBackStack()
                }
            })
        }
    }

    private fun openCalendarDialog(selectedDateInMillis: Long?) {
        val selectedDate = Calendar.getInstance()
        selectedDateInMillis?.let {
            selectedDate.timeInMillis = it
        }
        context?.let {
            DatePickerDialog(it, listener,
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH))
                    .show()
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
        private const val ARG_DATE = "ARG_DATE"
        private const val EMPTY_DATE = -1L
        @JvmStatic
        fun startMe(title: String?, hint: String?, text: String?, date: Long?) =
                EditNoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_HINT, hint)
                        putString(ARG_TEXT, text)
                        putLong(ARG_DATE, date ?: -1)
                    }
                }
    }

    override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule())
                .inject(this)
    }

}
