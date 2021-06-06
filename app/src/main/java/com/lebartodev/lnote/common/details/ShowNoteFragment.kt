package com.lebartodev.lnote.common.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.lebartodev.core.db.entity.Note
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.EditorEventContainer
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.di.notes.DaggerNotesComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.extensions.animateSlideTopVisibility
import com.lebartodev.lnote.utils.extensions.onLayout
import com.lebartodev.lnote.utils.ui.DateChip
import com.lebartodev.lnote.utils.ui.toPx
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ShowNoteFragment : BaseFragment() {
    private lateinit var formatter: SimpleDateFormat
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var dateChip: DateChip
    private lateinit var divider: View
    private lateinit var noteContent: NestedScrollView
    private lateinit var viewModel: ShowNoteViewModel
    private lateinit var actionBarTitleTextView: TextView
    private lateinit var backButton: View

    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        context?.run {
            DaggerNotesComponent.builder()
                    .context(this)
                    .appComponent(LNoteApplication[this].appComponent)
                    .context(this)
                    .build()
                    .inject(this@ShowNoteFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show_note, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatter = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        editButton = view.findViewById(R.id.edit_button)
        deleteButton = view.findViewById(R.id.delete_button)
        dateChip = view.findViewById(R.id.date_chip)
        divider = view.findViewById(R.id.add_divider)
        noteContent = view.findViewById(R.id.note_content)
        actionBarTitleTextView = view.findViewById(R.id.text_title_action_bar)
        backButton = view.findViewById(R.id.back_button)
        val id = arguments?.getLong(EXTRA_ID)

        descriptionTextView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && !descriptionTextView.hasSelection()) {
                editButton.callOnClick()
                true
            } else {
                false
            }
        }
        titleTextView.setOnClickListener {
            editButton.callOnClick()
        }

        view.transitionName = resources.getString(R.string.note_container_transition_name, id?.toString() ?: "local")
        noteContent.transitionName = resources.getString(R.string.note_content_transition_name, id?.toString() ?: "local")
        titleTextView.transitionName = resources.getString(R.string.note_title_transition_name, id?.toString() ?: "local")
        descriptionTextView.transitionName = resources.getString(R.string.note_description_transition_name, id?.toString() ?: "local")
        dateChip.transitionName = resources.getString(R.string.note_date_transition_name, id?.toString() ?: "local")

        backButton.setOnClickListener { fragmentManager?.popBackStack() }

        val visibleTitleLimit = 56f.toPx(resources)
        noteContent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY >= visibleTitleLimit && oldScrollY < visibleTitleLimit) {
                actionBarTitleTextView.animate().cancel()
                actionBarTitleTextView.animate().alpha(1f).start()
            } else if (scrollY < visibleTitleLimit && oldScrollY > visibleTitleLimit) {
                actionBarTitleTextView.animate().cancel()
                actionBarTitleTextView.animate().alpha(0f).start()
            }
        }


        viewModel = ViewModelProvider(this, viewModelFactory)[ShowNoteViewModel::class.java]

        viewModel.note().observe(this, Observer { note ->
            note.run {
                titleTextView.text = title
                actionBarTitleTextView.text = title
                descriptionTextView.text = text
                dateChip.setDate(date)
                setupEditButton(this)
                startPostponedEnterTransition()
                deleteButton.setOnClickListener {
                    viewModel.delete()
                }
            }
        })
        viewModel.error().observe(this, Observer { error ->
//            when(error){
//                is ShowNoteViewModel.LoadNoteException ->
//            }
        })
        viewModel.deleteResult().observe(this, Observer { status ->
            if (status == true) {
                (activity as EditorEventContainer).deleteNote()
                sharedElementReturnTransition = null
                fragmentManager?.popBackStack()
            }
        })
        id?.run { viewModel.loadNote(this) }
    }

    private fun setupEditButton(note: Note) {
        editButton.setOnClickListener {
            note.id?.run {
                val nextFragment = EditNoteFragment.initMe(this, scrollY = noteContent.scrollY)

                nextFragment.sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                fragmentManager
                        ?.beginTransaction()
                        ?.setReorderingAllowed(true)
                        ?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addSharedElement(noteContent, noteContent.transitionName)
                        ?.addSharedElement(dateChip, dateChip.transitionName)
                        ?.addToBackStack(null)
                        ?.commit()
            }
        }
    }

    override fun onStartSharedAnimation(sharedElementNames: MutableList<String>) {
        super.onStartSharedAnimation(sharedElementNames)
        deleteButton.onLayout {
            deleteButton.visibility = View.GONE
            deleteButton.animateSlideTopVisibility(true)
        }
        editButton.onLayout {
            editButton.visibility = View.GONE
            editButton.animateSlideTopVisibility(true)
        }
        backButton.onLayout {
            backButton.visibility = View.GONE
            backButton.animateSlideTopVisibility(true)
        }
    }

    companion object {
        const val BACK_STACK_TAG = "ShowNote.BACK_STACK_TAG"
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_FROM_ARCHIVE = "EXTRA_FROM_ARCHIVE"

        fun initMe(id: Long, fromArchive: Boolean = false): ShowNoteFragment {
            val fragment = ShowNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            args.putBoolean(EXTRA_FROM_ARCHIVE, fromArchive)
            fragment.arguments = args
            return fragment
        }
    }

}