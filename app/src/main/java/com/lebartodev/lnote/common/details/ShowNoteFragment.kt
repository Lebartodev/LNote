package com.lebartodev.lnote.common.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.lebartodev.lnote.R
import com.lebartodev.lnote.base.BaseFragment
import com.lebartodev.lnote.common.edit.EditNoteFragment
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.Status
import com.lebartodev.lnote.di.app.AppComponent
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.ui.DateChip
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

    private lateinit var viewModel: ShowNoteViewModel
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory

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

        view.findViewById<View>(R.id.back_button).setOnClickListener { fragmentManager?.popBackStack() }

        val id = arguments?.getLong(EXTRA_ID)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[ShowNoteViewModel::class.java]

        viewModel.note().observe(this, Observer { vmObject ->
            if (vmObject.status == Status.SUCCESS) {
                vmObject.run {
                    titleTextView.text = data?.title
                    descriptionTextView.text = data?.text
                    dateChip.setDate(data?.date)
                    data?.run { setupEditButton(this) }
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
                val nextFragment = EditNoteFragment.initMe(this)
                exitTransition = Fade(Fade.OUT)
                        .apply { duration = resources.getInteger(R.integer.animation_duration).toLong() / 2 }

                returnTransition = Fade(Fade.IN)
                        .apply { duration = resources.getInteger(R.integer.animation_duration).toLong() }

                nextFragment.enterTransition = Fade(Fade.IN)
                        .apply {
                            startDelay = resources.getInteger(R.integer.animation_duration).toLong() / 2
                            duration = resources.getInteger(R.integer.animation_duration).toLong() / 2
                        }


                nextFragment.sharedElementEnterTransition = TransitionSet()
                        .apply {
                            addTransition(TransitionInflater.from(context).inflateTransition(android.R.transition.move))
                            startDelay = 0
                            duration = resources.getInteger(R.integer.animation_duration).toLong()
                        }

                fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.notes_layout_container, nextFragment)
                        ?.addSharedElement(titleTextView, titleTextView.transitionName)
                        ?.addSharedElement(background, background.transitionName)
                        ?.addSharedElement(descriptionTextView, descriptionTextView.transitionName)
                        ?.addSharedElement(dateChip, dateChip.transitionName)
                        ?.addSharedElement(divider, divider.transitionName)
                        ?.addToBackStack(null)
                        ?.commit()
            }

        }
    }

    override fun setupComponent(component: AppComponent) {
        component.plus(NotesModule()).inject(this)
    }

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"

        fun initMe(id: Long): ShowNoteFragment {
            val fragment = ShowNoteFragment()
            val args = Bundle()
            args.putLong(EXTRA_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

}