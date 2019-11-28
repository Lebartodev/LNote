package com.lebartodev.lnote.common.details


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class NoteCreationView : ConstraintLayout {
    private var viewModel: NoteEditViewModel? = null

    private val saveNoteButton: MaterialButton
    private val titleText: EditText
    private val descriptionText: EditText
    private val fabMore: FloatingActionButton
    private val background: View
    private val fullScreenButton: ImageButton
    private val calendarButton: ImageButton
    private val deleteButton: ImageButton
    private val divider: View
    private val dateChip: Chip

    private var fragment: Fragment? = null
    private var fullScreenClickListener: FullScreenClickListener? = null
    var isMoreOpen = false

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
            viewModel?.setDescription(s?.toString() ?: "")
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

        setupSheetView()
    }

    fun setupFragment(fragment: Fragment, viewModelFactory: LNoteViewModelFactory,
                      isMoreOpen: Boolean, fullScreenClickListener: FullScreenClickListener) {
        this.isMoreOpen = isMoreOpen
        this.fullScreenClickListener = fullScreenClickListener
        this.fragment = fragment
        this.viewModel = ViewModelProviders.of(fragment,
                viewModelFactory)[NoteEditViewModel::class.java]
        if (isMoreOpen) {
            deleteButton.visibility = View.VISIBLE
            calendarButton.visibility = View.VISIBLE
            fabMore.setImageResource(R.drawable.ic_arrow_right_24)
        }
        viewModel?.apply {
            currentNote().observe(fragment, Observer { noteData ->
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
                titleText.setText(title)
                descriptionText.setText(description)

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
            })

            saveResult().observe(fragment, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, context.getString(R.string.error_note_create),
                            Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSheetView() {
        fullScreenButton.setOnClickListener {
            (fragment as BaseFragment).hideKeyboardListener(titleText) {
                val nextFragment = EditNoteFragment
                        .initMe(titleText.text.toString(),
                                titleText.hint.toString(),
                                descriptionText.text.toString(),
                                viewModel?.selectedDate()?.value)

                fullScreenClickListener?.onFullScreenClicked()

                fragment?.exitTransition = Fade(Fade.OUT)
                        .apply {
                            duration = resources
                                    .getInteger(R.integer.animation_duration).toLong() / 2
                        }

                fragment?.returnTransition = Fade(Fade.IN)
                        .apply {
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                nextFragment.enterTransition = Fade(Fade.IN)
                        .apply {
                            startDelay = resources
                                    .getInteger(R.integer.animation_duration).toLong() / 2
                            duration = resources
                                    .getInteger(R.integer.animation_duration).toLong() / 2
                        }


                nextFragment.sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(TransitionInflater.from(context)
                                    .inflateTransition(android.R.transition.move))
                            startDelay = 0
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                val transaction = fragment?.fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addSharedElement(titleText, titleText.transitionName)
                        ?.addSharedElement(background, background.transitionName)
                        ?.addSharedElement(saveNoteButton, saveNoteButton.transitionName)
                        ?.addSharedElement(descriptionText, descriptionText.transitionName)
                        ?.addSharedElement(fullScreenButton, fullScreenButton.transitionName)
                        ?.addSharedElement(dateChip, dateChip.transitionName)
                        ?.addSharedElement(divider, divider.transitionName)
                        ?.addToBackStack(null)
                if (deleteButton.visibility == View.VISIBLE && calendarButton.visibility == View.VISIBLE) {
                    transaction?.addSharedElement(deleteButton, deleteButton.transitionName)
                            ?.addSharedElement(calendarButton, calendarButton.transitionName)
                }
                transaction?.commit()
            }
        }

        fabMore.setOnClickListener {
            val constraintLayout = this
            TransitionManager.beginDelayedTransition(constraintLayout)
            isMoreOpen = !isMoreOpen
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.setVisibility(calendarButton.id,
                    if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            set.setVisibility(deleteButton.id,
                    if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            fabMore.setImageResource(
                    if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
            set.applyTo(constraintLayout)
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

    interface FullScreenClickListener {
        fun onFullScreenClicked()
    }
}
