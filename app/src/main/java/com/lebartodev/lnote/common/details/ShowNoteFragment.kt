package com.lebartodev.lnote.common.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.notes.DaggerNotesComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.ui.DateChip
import com.lebartodev.lnote.utils.ui.toPx
import com.lebartodev.lnote.utils.ui.toast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ShowNoteFragment : BaseFragment() {
    private lateinit var formatter: SimpleDateFormat
    private lateinit var descriptionTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var editButton: ImageButton
    private lateinit var dateChip: DateChip
    private lateinit var background: View
    private lateinit var divider: View
    private lateinit var noteContent: NestedScrollView
    private lateinit var viewModel: ShowNoteViewModel
    private lateinit var actionBarTitleTextView: TextView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        formatter = SimpleDateFormat(resources.getString(R.string.date_pattern), Locale.US)
        titleTextView = view.findViewById(R.id.text_title)
        descriptionTextView = view.findViewById(R.id.text_description)
        editButton = view.findViewById(R.id.edit_button)
        dateChip = view.findViewById(R.id.date_chip)
        background = view.findViewById(R.id.note_creation_background)
        divider = view.findViewById(R.id.add_divider)
        noteContent = view.findViewById(R.id.note_content)
        actionBarTitleTextView = view.findViewById(R.id.text_title_action_bar)
        val id = arguments?.getLong(EXTRA_ID)
        view.transitionName = resources.getString(R.string.note_content_transition_name, id)

        view.findViewById<View>(R.id.back_button).setOnClickListener { fragmentManager?.popBackStack() }

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


        viewModel = ViewModelProviders.of(this, viewModelFactory)[ShowNoteViewModel::class.java]

        viewModel.note().observe(this, Observer { vmObject ->
            if (vmObject.status == Status.SUCCESS) {
                vmObject.run {
                    titleTextView.text = data?.title
                    actionBarTitleTextView.text = data?.title
                    descriptionTextView.text = data?.text
                    dateChip.setDate(data?.date)
                    data?.run { setupEditButton(this) }
                    startPostponedEnterTransition()
                }
            } else if (vmObject.status == Status.ERROR) {
                toast(vmObject.error?.message)
            }
        })
        id?.run { viewModel.loadNote(this) }
    }

    private fun setupEditButton(note: Note) {
        editButton.setOnClickListener {
            note.id?.run {
                val nextFragment = EditNoteFragment.initMe(this, note.title, note.text, noteContent.scrollY)

                nextFragment.sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                            startDelay = 0
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                fragmentManager
                        ?.beginTransaction()
                        ?.setReorderingAllowed(true)
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addSharedElement(noteContent, noteContent.transitionName)
                        ?.addSharedElement(background, background.transitionName)
                        ?.addSharedElement(dateChip, dateChip.transitionName)
                        ?.addToBackStack(null)
                        ?.commit()
            }

        }
    }


    companion object {
        const val BACK_STACK_TAG = "ShowNote.BACK_STACK_TAG"
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_CONTENT_TRANSITION_NAME = "EXTRA_CONTENT_TRANSITION_NAME"

        fun initMe(id: Long): ShowNoteFragment {
            val fragment = ShowNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

}