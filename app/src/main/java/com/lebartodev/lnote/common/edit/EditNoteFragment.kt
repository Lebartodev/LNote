package com.lebartodev.lnote.common.edit

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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class EditNoteFragment : BaseFragment() {
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var fullScreenButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveNoteButton: MaterialButton
    private lateinit var calendarButton: ImageButton
    private lateinit var dateChip: Chip
    private lateinit var backButton: ImageButton
    private var noteId: Long? = null

    private val noteObserver: Observer<NoteEditViewModel.NoteData> = Observer { noteData ->
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != titleTextView.hint) {
            if (description.isNotEmpty()) {
                titleTextView.hint = viewModel.getFormattedHint(description)
            } else {
                titleTextView.hint = context?.getString(R.string.title_hint)
            }
        }

        if (titleTextView.text.toString() != title) {
            titleTextView.text = title
        }
        if (descriptionTextView.text.toString() != description) {
            descriptionTextView.text = description
        }
        if (time != null) {
            dateChip.visibility = View.VISIBLE
            dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US).format(Date(time))
        } else {
            dateChip.visibility = View.GONE
        }
        if (noteData.id == noteId && noteId != null)
            startPostponedEnterTransition()
    }

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory
    private lateinit var viewModel: NoteEditViewModel
    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.setDescription(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel.setTitle(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        noteId = arguments?.getLong(EXTRA_ID)
        if (noteId != null) {
            postponeEnterTransition()
        }
        super.onViewCreated(view, savedInstanceState)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        fullScreenButton = view.findViewById(R.id.full_screen_button)
        saveNoteButton = view.findViewById(R.id.save_button)
        deleteButton = view.findViewById(R.id.delete_button)
        dateChip = view.findViewById(R.id.date_chip)
        calendarButton = view.findViewById(R.id.calendar_button)
        backButton = view.findViewById(R.id.back_button)
        arguments?.getString(EXTRA_TEXT)?.run { descriptionTextView.text = this }
        arguments?.getString(EXTRA_TITLE)?.run { titleTextView.text = this }


        descriptionTextView.addTextChangedListener(descriptionTextWatcher)
        titleTextView.addTextChangedListener(titleTextWatcher)
        if (noteId != null) {
            backButton.setOnClickListener {
                hideKeyboard()
                fragmentManager?.popBackStack()
            }
            backButton.visibility = View.VISIBLE
            fullScreenButton.visibility = View.GONE
        } else {
            backButton.visibility = View.GONE
            fullScreenButton.visibility = View.VISIBLE
            fullScreenButton.setOnClickListener { fragmentManager?.popBackStack() }
        }

        deleteButton.setOnClickListener { if (noteId == null) viewModel.clearCurrentNote() else viewModel.deleteEditedNote() }
        saveNoteButton.setOnClickListener {
            hideKeyboard()
            viewModel.currentNote().removeObserver(noteObserver)
            viewModel.saveNote()
        }

        calendarButton.setOnClickListener { openCalendarDialog() }
        dateChip.setOnClickListener { openCalendarDialog() }
        dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        setupEditViewModel()
        if (savedInstanceState == null)
            noteId?.run { viewModel.loadNote(this) }
    }

    private fun setupEditViewModel() {
        viewModel = activity?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        viewModel.showNoteDeleted().observe(viewLifecycleOwner, Observer {
            if (it == true) {
                titleTextView.clearFocus()
                descriptionTextView.clearFocus()
                if (noteId == null) {
                    fragmentManager?.popBackStack()
                } else {
                    fragmentManager?.popBackStack(ShowNoteFragment.BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        })
        viewModel.saveResult().observe(viewLifecycleOwner, Observer { obj ->
            if (obj != null) {
                if (obj.status == Status.ERROR) {
                    viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
                    Toast.makeText(context, getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                } else if (obj.status == Status.SUCCESS) {
                    fragmentManager?.popBackStack()
                }
            }
        })
        viewModel.currentNote().observe(viewLifecycleOwner, noteObserver)
        viewModel.dateDialog().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                context?.run {
                    val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, y, m, d -> viewModel.setDate(y, m, d) },
                            it.get(Calendar.YEAR),
                            it.get(Calendar.MONTH),
                            it.get(Calendar.DAY_OF_MONTH))
                    dialog.setOnDismissListener { viewModel.closeDateDialog() }
                    dialog.show()
                }
            }
        })
    }

    private fun openCalendarDialog() {
        viewModel.openDateDialog()
    }

    override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule()).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (noteId != null)
            viewModel.resetCurrentNote()
    }


    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"

        fun initMe() = EditNoteFragment()

        fun initMe(id: Long, title: String?, text: String?): EditNoteFragment {
            val fragment = EditNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }
}