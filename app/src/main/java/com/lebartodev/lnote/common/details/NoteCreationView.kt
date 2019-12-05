package com.lebartodev.lnote.common.details


import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.notes.NotesModule
import java.text.SimpleDateFormat
import java.util.*

class NoteCreationView : ConstraintLayout {
    val saveNoteButton: MaterialButton
    val titleText: EditText
    val descriptionText: EditText
    private val fabMore: FloatingActionButton
    val background: View
    val fullScreenButton: ImageButton
    val calendarButton: ImageButton
    val deleteButton: ImageButton
    val divider: View
    val dateChip: Chip

    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel?.setDescription(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            viewModel?.setTitle(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        viewModel?.setDate(y, m, d)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    init {
        inflate(context, R.layout.view_note_creation, this)
        context?.let { LNoteApplication[it].component().plus(NotesModule()).inject(this) }
        calendarButton = findViewById(R.id.calendar_button)
        deleteButton = findViewById(R.id.delete_button)
        background = findViewById(R.id.note_creation_background)
        saveNoteButton = findViewById(R.id.save_button)
        titleText = findViewById(R.id.text_title)
        descriptionText = findViewById(R.id.text_description)
        fabMore = findViewById(R.id.fab_more)
        fullScreenButton = findViewById(R.id.full_screen_button)
        divider = findViewById(R.id.add_divider)
        dateChip = findViewById(R.id.date_chip)

        saveNoteButton.setOnClickListener {
            viewModel?.saveNote()
            titleText.setText("")
            descriptionText.setText("")
            dateChip.text = ""
            dateChip.visibility = View.GONE
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        deleteButton.setOnClickListener {
            viewModel?.clearCurrentNote()
            dateChip.visibility = View.GONE
            dateChip.text = ""
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        dateChip.setOnCloseIconClickListener {
            viewModel?.clearDate()
        }
        calendarButton.setOnClickListener {
            openCalendarDialog(null)
        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        fullScreenButton.setOnClickListener { viewModel?.toggleFullScreen() }
        fabMore.setOnClickListener { viewModel?.toggleMore() }

        viewModel = getActivity()?.run { ViewModelProviders.of(this, viewModelFactory)[NoteEditViewModel::class.java] } ?: throw NullPointerException()
        if (viewModel?.isMoreOpen()?.value == true) {
            deleteButton.visibility = View.VISIBLE
            calendarButton.visibility = View.VISIBLE
            fabMore.setImageResource(R.drawable.ic_arrow_right_24)
        }
    }

    private fun openCalendarDialog(selectedDateInMillis: Long?) {
        val selectedDate = Calendar.getInstance()
        selectedDateInMillis?.let {
            selectedDate.timeInMillis = it
        }
        selectedDate?.let {
            DatePickerDialog(context, listener,
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH),
                    it.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }

    override fun onDetachedFromWindow() {
        descriptionText.removeTextChangedListener(descriptionTextWatcher)
        titleText.removeTextChangedListener(titleTextWatcher)
        super.onDetachedFromWindow()
    }

    fun updateNoteData(noteData: NoteEditViewModel.NoteData) {
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != titleText.hint) {
            if (description.isNotEmpty()) {
                titleText.hint = viewModel?.getFormattedHint(description)
            } else {
                titleText.hint = context?.getString(R.string.title_hint)
            }
        }
        if (titleText.text.toString() != title) {
            titleText.setText(title)
        }
        if (descriptionText.text.toString() != description) {
            descriptionText.setText(description)
        }

        if (time != null) {
            dateChip.visibility = View.VISIBLE
            dateChip.text = SimpleDateFormat(resources.getString(R.string.date_pattern),
                    Locale.US).format(Date(time))
        } else {
            dateChip.visibility = View.GONE
        }
        calendarButton.setOnClickListener {
            openCalendarDialog(time)
        }
        dateChip.setOnClickListener {
            openCalendarDialog(time)
        }
    }

    fun updateMoreState(isMoreOpen: Boolean) {
        val constraintLayout = this
        TransitionManager.beginDelayedTransition(constraintLayout)
        val set = ConstraintSet()
        set.clone(constraintLayout)
        set.setVisibility(calendarButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
        set.setVisibility(deleteButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
        fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
        set.applyTo(constraintLayout)
    }
}
