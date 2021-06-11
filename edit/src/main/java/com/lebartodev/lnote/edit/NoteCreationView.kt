package com.lebartodev.lnote.edit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.ui.DateChip
import com.lebartodev.lnote.utils.ui.NoteTransitionDrawable
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import java.util.Calendar
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class NoteCreationView : ConstraintLayout {
    private val saveNoteButton: MaterialButton
    private val titleText: EditText
    private val descriptionText: EditText
    private val fabMore: FloatingActionButton
    private val fullScreenButton: ImageButton
    private val calendarButton: ImageButton
    private val deleteButton: ImageButton
    private val dateChip: DateChip
    private val noteContent: NestedScrollView

    var saveListener: (() -> Unit)? = null
    var fullScreenListener: (() -> Unit)? = null

    private var isMoreOpen = false

    @Inject
    lateinit var viewModelFactory: EditNoteViewModelFactory
    private val viewModel: NoteEditViewModel by lazy { ViewModelProvider(context as ViewModelStoreOwner, viewModelFactory)[NoteEditViewModel::class.java] }

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

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_note_creation, this)

        DaggerEditNoteComponent.builder()
            .appComponent((context.applicationContext as AppComponentProvider).provideAppComponent())
            .context(context)
            .build()
            .inject(this)
        isSaveEnabled = true

        calendarButton = findViewById(R.id.calendar_button)
        deleteButton = findViewById(R.id.delete_button)
        saveNoteButton = findViewById(R.id.save_button)
        titleText = findViewById(R.id.text_title)
        descriptionText = findViewById(R.id.text_description)
        fabMore = findViewById(R.id.fab_more)
        fullScreenButton = findViewById(R.id.full_screen_button)
        dateChip = findViewById(R.id.date_chip)
        noteContent = findViewById(R.id.note_content)

        background = NoteTransitionDrawable(ContextCompat.getColor(context, R.color.white), 0f)


        transitionName = resources.getString(R.string.note_container_transition_name, "local")
        noteContent.transitionName = resources.getString(R.string.note_content_transition_name, "local")
        titleText.transitionName = resources.getString(R.string.note_title_transition_name, "local")
        descriptionText.transitionName = resources.getString(R.string.note_description_transition_name, "local")
        dateChip.transitionName = resources.getString(R.string.note_date_transition_name, "local")

        saveNoteButton.setOnClickListener {
            viewModel.saveNote()
            // closeNoteCreation()
        }
        deleteButton.setOnClickListener {
            viewModel.deleteEditedNote()
            // TODO:closeNoteCreation()
        }
        dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        calendarButton.setOnClickListener { openCalendarDialog(null) }
        descriptionText.addTextChangedListener(descriptionTextWatcher)
        titleText.addTextChangedListener(titleTextWatcher)

        fullScreenButton.setOnClickListener { fullScreenListener?.invoke() }
        fabMore.setOnClickListener { setMoreOpen(!isMoreOpen) }
    }

    override fun onSaveInstanceState(): Parcelable {
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
        selectedDate.let {
            (context as FragmentActivity).supportFragmentManager.run {
                val dialog = SelectDateFragment.initMe(it)
                dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d -> viewModel.setDate(y, m, d) }
                dialog.show(this, TAG_CALENDAR_DIAlOG)
            }
        }
    }

    override fun onDetachedFromWindow() {
        descriptionText.removeTextChangedListener(descriptionTextWatcher)
        titleText.removeTextChangedListener(titleTextWatcher)
        super.onDetachedFromWindow()
    }

    fun updateNoteData(noteData: NoteData) {
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != titleText.hint) {
            if (description.isNotEmpty()) {
                titleText.hint = description.formattedHint()
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
        dateChip.setDate(time)
        calendarButton.setOnClickListener {
            openCalendarDialog(time)
        }
        dateChip.setOnClickListener {
            openCalendarDialog(time)
        }
    }

    private fun setMoreOpen(isMoreOpen: Boolean, animate: Boolean = true) {
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

    fun getSharedViews(): List<View> {
        val result = mutableListOf(this, noteContent, saveNoteButton, fullScreenButton, dateChip)
        if (isMoreOpen) {
            result.addAll(listOf(deleteButton, calendarButton))
        }
        return result
    }

    fun getContentScroll(): Int = noteContent.scrollY

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
    companion object {
        private const val TAG_CALENDAR_DIAlOG = "TAG_CALENDAR_DIAlOG"
    }
}
