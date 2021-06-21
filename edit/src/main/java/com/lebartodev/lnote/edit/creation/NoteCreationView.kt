package com.lebartodev.lnote.edit.creation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.lebartodev.core.data.NoteData
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.lnote.edit.EditNoteViewModelFactory
import com.lebartodev.lnote.edit.NoteEditViewModel
import com.lebartodev.lnote.edit.R
import com.lebartodev.lnote.edit.databinding.ViewNoteCreationBinding
import com.lebartodev.lnote.edit.di.DaggerEditNoteComponent
import com.lebartodev.lnote.edit.di.EditNoteComponent
import com.lebartodev.lnote.utils.extensions.formattedHint
import com.lebartodev.lnote.utils.ui.NoteTransitionDrawable
import com.lebartodev.lnote.utils.ui.SelectDateFragment
import java.util.*
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class NoteCreationView : ConstraintLayout {
    private var component: EditNoteComponent? = null

    private var binding = ViewNoteCreationBinding.inflate(LayoutInflater.from(context), this)
    private val noteObserver = Observer<NoteData> {
        updateNoteData(it)
    }
    var closeListener: (() -> Unit)? = null
    var fullScreenListener: (() -> Unit)? = null

    private var isMoreOpen = false

    @Inject
    lateinit var viewModelFactory: EditNoteViewModelFactory
    private val viewModel: NoteEditViewModel by lazy {
        ViewModelProvider(findFragment<Fragment>().requireParentFragment(), viewModelFactory)[NoteEditViewModel::class.java]
    }

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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
        defStyleAttr)


    init {
        isSaveEnabled = true

        background = NoteTransitionDrawable(ContextCompat.getColor(context, R.color.white), 0f)

        transitionName = resources.getString(R.string.note_container_transition_name, "local")
        binding.noteContent.transitionName = resources.getString(
            R.string.note_content_transition_name, "local")
        binding.textTitle.transitionName = resources.getString(R.string.note_title_transition_name,
            "local")
        binding.textDescription.transitionName = resources.getString(
            R.string.note_description_transition_name, "local")
        binding.dateChip.transitionName = resources.getString(R.string.note_date_transition_name,
            "local")

        binding.saveButton.setOnClickListener {
            viewModel.saveNote()
            closeListener?.invoke()
        }
        binding.deleteButton.setOnClickListener {
            viewModel.deleteEditedNote()
            closeListener?.invoke()
        }

        binding.dateChip.setOnCloseIconClickListener { viewModel.clearDate() }
        binding.calendarButton.setOnClickListener { openCalendarDialog(null) }
        binding.textDescription.addTextChangedListener(descriptionTextWatcher)
        binding.textTitle.addTextChangedListener(titleTextWatcher)

        binding.fullScreenButton.setOnClickListener { fullScreenListener?.invoke() }
        binding.fabMore.setOnClickListener { setMoreOpen(!isMoreOpen) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        component = DaggerEditNoteComponent.builder()
            .appComponent(
                (context.applicationContext as AppComponentProvider).provideAppComponent())
            .context(context)
            .build().also { it.inject(this) }
        viewModel.currentNote().observeForever(noteObserver)
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
                dialog.listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    viewModel.setDate(y, m, d)
                }
                dialog.show(this, TAG_CALENDAR_DIAlOG)
            }
        }
    }

    override fun onDetachedFromWindow() {
        binding.textDescription.removeTextChangedListener(descriptionTextWatcher)
        binding.textTitle.removeTextChangedListener(titleTextWatcher)
        viewModel.currentNote().removeObserver(noteObserver)
        super.onDetachedFromWindow()
        component = null
    }

    fun updateNoteData(noteData: NoteData) {
        val description = noteData.text ?: ""
        val title = noteData.title
        val time = noteData.date

        if (description != binding.textTitle.hint) {
            if (description.isNotEmpty()) {
                binding.textTitle.hint = description.formattedHint()
            } else {
                binding.textTitle.hint = context?.getString(R.string.title_hint)
            }
        }
        if (binding.textTitle.text.toString() != title) {
            binding.textTitle.setText(title)
        }
        if (binding.textDescription.text.toString() != description) {
            binding.textDescription.setText(description)
        }
        binding.dateChip.setDate(time)
        binding.calendarButton.setOnClickListener {
            openCalendarDialog(time)
        }
        binding.dateChip.setOnClickListener {
            openCalendarDialog(time)
        }
    }

    private fun setMoreOpen(isMoreOpen: Boolean, animate: Boolean = true) {
        if (animate) {
            val constraintLayout = this
            TransitionManager.beginDelayedTransition(constraintLayout)
            val set = ConstraintSet()
            set.clone(constraintLayout)
            set.setVisibility(binding.calendarButton.id,
                if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            set.setVisibility(binding.deleteButton.id,
                if (isMoreOpen) ConstraintSet.VISIBLE else ConstraintSet.GONE)
            binding.fabMore.setImageResource(
                if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
            set.applyTo(constraintLayout)
        } else {
            binding.deleteButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            binding.calendarButton.visibility = if (isMoreOpen) View.VISIBLE else View.GONE
            binding.fabMore.setImageResource(
                if (isMoreOpen) R.drawable.ic_arrow_right_24 else R.drawable.ic_drop_down_24)
        }
        this.isMoreOpen = isMoreOpen
    }

    fun getSharedViews(): List<View> {
        val result = mutableListOf(this, binding.noteContent, binding.saveButton,
            binding.fullScreenButton, binding.dateChip)
        if (isMoreOpen) {
            result.addAll(listOf(binding.deleteButton, binding.calendarButton))
        }
        return result
    }

    fun getContentScroll(): Int = binding.noteContent.scrollY

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
