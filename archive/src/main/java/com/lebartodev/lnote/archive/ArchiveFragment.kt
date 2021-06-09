package com.lebartodev.lnote.archive

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.lnote.common.LNoteApplication
import com.lebartodev.lnote.common.details.ShowNoteFragment
import com.lebartodev.lnote.common.notes.NotesAdapter
import com.lebartodev.lnote.di.notes.DaggerNotesComponent
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.ui.*
import javax.inject.Inject

class ArchiveFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ArchiveViewModelFactory
    private lateinit var archiveViewModel: ArchiveViewModel
    private lateinit var notesList: RecyclerView

    private val adapter = NotesAdapter { note, sharedViews ->
        note.id?.run {
//            val nextFragment = ShowNoteFragment.initMe(this@run)
//            val transition = TransitionSet()
//            transition.addTransition(ChangeTransform())
//            transition.addTransition(ChangeImageTransform())
//            transition.addTransition(ChangeBounds())
//            transition.addTransition(ChangeClipBounds())
//            transition.addTransition(CardExpandTransition())
//            transition.interpolator = LinearOutSlowInInterpolator()
//            transition.duration = resources.getInteger(R.integer.animation_duration).toLong()
//            nextFragment.sharedElementEnterTransition = transition
//            sharedElementReturnTransition = transition
//
//            fragmentManager?.beginTransaction()?.run {
//                setReorderingAllowed(true)
//                setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
//                sharedViews.forEach { addSharedElement(it, it.transitionName) }
//                replace(R.id.notes_layout_container, nextFragment)
//                addToBackStack(ShowNoteFragment.BACK_STACK_TAG)
//                commit()
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            if ((application as LNoteApplication).notesComponent == null) {
                (application as LNoteApplication).notesComponent = DaggerNotesComponent.builder()
                        .appComponent(LNoteApplication[this].appComponent)
                        .context(this)
                        .build()
            }
            (activity?.application as LNoteApplication).notesComponent?.inject(this@ArchiveFragment)
        }
        archiveViewModel = ViewModelProvider(this, viewModelFactory)[ArchiveViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesList = view.findViewById(R.id.notes_list)
        notesList.layoutManager = LinearLayoutManager(context)
        notesList.adapter = adapter
        notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))

        archiveViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })

    }
}
