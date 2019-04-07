package com.lebartodev.lnote.common.creation


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Parcelable
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.repository.CurrentNote
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import java.util.*

class NoteCreationView : ConstraintLayout {
    private var notesViewModel: NoteEditViewModel? = null

    private val saveNoteButton: MaterialButton
    private lateinit var titleText: EditText
    private val descriptionText: EditText
    private val fabMore: FloatingActionButton
    private val background: View
    private val fullScreenButton: ImageButton
    private val calendarButton: ImageButton
    private val deleteButton: ImageButton
    private var fragment: Fragment? = null
    private val divider: View

    private var clickListener: ClickListener? = null
    var isMoreOpen = false
    fun setupFragment(fragment: Fragment, viewModelFactory: LNoteViewModelFactory, clickListener: ClickListener,
                      isMoreOpen: Boolean) {
        this.isMoreOpen = isMoreOpen
        this.clickListener = clickListener
        this.fragment = fragment
        this.notesViewModel = ViewModelProviders.of(fragment, viewModelFactory)[NoteEditViewModel::class.java]
        if (isMoreOpen) {
            deleteButton.visibility = View.VISIBLE
            calendarButton.visibility = View.VISIBLE
            fabMore.setImageResource(R.drawable.ic_arrow_right_24)
        }
        titleText.setText(CurrentNote.title)
        descriptionText.setText(CurrentNote.text)
        this.notesViewModel?.apply {
            descriptionTextLiveData.observe(fragment, Observer {
                if (it != null) {
                    if (it.isNotEmpty() && it != titleText.hint)
                        titleText.hint = it
                    else
                        titleText.hint = context.getString(R.string.title_hint)
                }
            })
            selectedDateString().observe(fragment, Observer {
                //dateText.setText(it)
            })
            getSaveResult().observe(fragment, Observer { obj ->
                if (obj.status == Status.ERROR) {
                    Toast.makeText(context, context.getString(R.string.error_note_create), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


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
    private val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
        notesViewModel?.setDate(y, m, d)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }

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

        saveNoteButton.setOnClickListener {
            clickListener?.onSaveClicked()
            notesViewModel?.saveNote(title = titleText.text.toString(), text = descriptionText.text.toString())
            titleText.setText("")
            descriptionText.setText("")
            titleText.clearFocus()
            descriptionText.clearFocus()
        }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        setupSheetView()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSheetView() {
        val moreHiddenConstraintSet = ConstraintSet()
        val moreExpandedConstraintSet = ConstraintSet()

        fullScreenButton.setOnClickListener {
            val nextFragment = NoteFragment.startMe(titleText.text.toString(), titleText.hint.toString(),
                    descriptionText.text.toString())
            clickListener?.onFullScreenClicked()

            val exitFade = Fade(Fade.OUT)
            exitFade.duration = ANIMATION_DURATION / 2
            fragment?.exitTransition = exitFade

            val returnFade = Fade(Fade.IN)
            returnFade.duration = ANIMATION_DURATION
            fragment?.returnTransition = returnFade


            val enterFade = Fade(Fade.IN)
            enterFade.startDelay = ANIMATION_DURATION / 2
            enterFade.duration = ANIMATION_DURATION / 2

            nextFragment.enterTransition = enterFade

            val enterTransitionSet = TransitionSet()
            enterTransitionSet.addTransition(
                    TransitionInflater.from(context).inflateTransition(android.R.transition.move))
            enterTransitionSet.startDelay = 0
            enterTransitionSet.duration = ANIMATION_DURATION

            nextFragment.sharedElementEnterTransition = enterTransitionSet

            val transaction = fragment?.fragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.notes_layout_container, nextFragment)
                    ?.addSharedElement(titleText, titleText.transitionName)
                    ?.addSharedElement(background, background.transitionName)
                    ?.addSharedElement(saveNoteButton, saveNoteButton.transitionName)
                    ?.addSharedElement(descriptionText, descriptionText.transitionName)
                    ?.addSharedElement(fullScreenButton, fullScreenButton.transitionName)
                    ?.addSharedElement(divider, divider.transitionName)
                    ?.addToBackStack(null)
            if (deleteButton.visibility == View.VISIBLE && calendarButton.visibility == View.VISIBLE) {
                transaction?.addSharedElement(deleteButton, deleteButton.transitionName)
                        ?.addSharedElement(calendarButton, calendarButton.transitionName)
            }
            transaction?.commit()
        }

        val constraintLayout = this
        moreHiddenConstraintSet.clone(constraintLayout)
        moreExpandedConstraintSet.clone(context, R.layout.view_note_creation_expanded)
        fabMore.setOnClickListener {
            TransitionManager.beginDelayedTransition(constraintLayout)
            val constraint = if (isMoreOpen) moreHiddenConstraintSet else moreExpandedConstraintSet
            isMoreOpen = !isMoreOpen
            fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
            constraint.applyTo(constraintLayout)
        }
    }

    private fun openCalendarDialog() {
        var selectedDate = notesViewModel?.selectedDate?.value
        if (selectedDate == null)
            selectedDate = Calendar.getInstance()
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

    interface ClickListener {
        fun onSaveClicked()
        fun onFullScreenClicked()
    }

    companion object {
        private const val ANIMATION_DURATION = 400L
    }
}
