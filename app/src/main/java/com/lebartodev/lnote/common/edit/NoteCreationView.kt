package com.lebartodev.lnote.common.edit


import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.NestedScrollView
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.di.notes.NotesModule
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ClickableViewAccessibility")
class NoteCreationView : ConstraintLayout {
    val saveNoteButton: MaterialButton
    private val titleText: EditText
    private val descriptionText: EditText
    private val fabMore: FloatingActionButton
    val background: View
    val fullScreenButton: ImageButton
    val calendarButton: ImageButton
    val deleteButton: ImageButton
    private val divider: View
    val dateChip: Chip
    val noteContent: NestedScrollView

    var descriptionListener: ((String) -> Unit)? = null
    var titleListener: ((String) -> Unit)? = null
    var saveListener: (() -> Unit)? = null
    var clearDateListener: (() -> Unit)? = null
    var clearNoteListener: (() -> Unit)? = null
    var fullScreenListener: (() -> Unit)? = null
    var formattedHintProducer: ((String) -> String)? = null
    var calendarDialogListener: ((Calendar) -> Unit)? = null

    private var isMoreOpen = false


    private val descriptionTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            descriptionListener?.invoke(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    private val titleTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            titleListener?.invoke(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
            defStyleAttr)

    init {
        inflate(context, R.layout.view_note_creation, this)
        isSaveEnabled = true
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
        noteContent = findViewById(R.id.note_content)

        saveNoteButton.setOnClickListener {
            saveListener?.invoke()
        }
        deleteButton.setOnClickListener {
            clearNoteListener?.invoke()
        }
        dateChip.setOnCloseIconClickListener { clearDateListener?.invoke() }
        calendarButton.setOnClickListener { openCalendarDialog(null) }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        fullScreenButton.setOnClickListener { fullScreenListener?.invoke() }
        fabMore.setOnClickListener { setMoreOpen(!isMoreOpen) }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.isMoreOpen = isMoreOpen
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            isMoreOpen = state.isMoreOpen
            setMoreOpen(isMoreOpen, false)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun openCalendarDialog(selectedDateInMillis: Long?) {
        val selectedDate = Calendar.getInstance()
        selectedDateInMillis?.let {
            selectedDate.timeInMillis = it
        }
        selectedDate?.let {
            calendarDialogListener?.invoke(it)
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
                titleText.hint = formattedHintProducer?.invoke(description)
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

    fun setMoreOpen(isMoreOpen: Boolean, animate: Boolean = true) {
        if (animate) {
            val constraintLayout = this
            TransitionManager.beginDelayedTransition(constraintLayout)
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.setVisibility(calendarButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            set.setVisibility(deleteButton.id, if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
            set.applyTo(constraintLayout)
        } else {
            deleteButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            calendarButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            fabMore.setImageResource(if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
        }
        this.isMoreOpen = isMoreOpen
    }

    private class SavedState : BaseSavedState {
        var isMoreOpen: Boolean = false

        constructor(parcel: Parcel) : super(parcel) {
            isMoreOpen = parcel.readByte().toInt() != 0
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeByte((if (isMoreOpen) 1 else 0).toByte())
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

}
