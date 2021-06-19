package com.lebartodev.lnote.archive

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.*
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.archive.databinding.FragmentArchiveBinding
import com.lebartodev.lnote.archive.di.DaggerArchiveComponent
import com.lebartodev.lnote.show.ShowNoteFragment
import com.lebartodev.lnote.utils.ui.*
import javax.inject.Inject

class ArchiveFragment : BaseFragment() {
    private val binding by viewBinding(FragmentArchiveBinding::inflate)

    @Inject
    lateinit var viewModelFactory: ArchiveViewModelFactory
    private val archiveViewModel: ArchiveViewModel by lazy { ViewModelProvider(this, viewModelFactory)[ArchiveViewModel::class.java] }

    private val adapter = NotesAdapter { note, sharedViews ->
        note.id?.run {
            val nextFragment = ShowNoteFragment.initMe(this@run)
            val transition = TransitionSet()
            transition.addTransition(ChangeTransform())
            transition.addTransition(ChangeImageTransform())
            transition.addTransition(ChangeBounds())
            transition.addTransition(ChangeClipBounds())
            transition.addTransition(CardExpandTransition())
            transition.interpolator = LinearOutSlowInInterpolator()
            transition.duration = resources.getInteger(R.integer.animation_duration).toLong()
            nextFragment.sharedElementEnterTransition = transition
            sharedElementReturnTransition = transition

            parentFragmentManager.beginTransaction().run {
                setReorderingAllowed(true)
                setCustomAnimations(0, R.anim.fade_out, 0, R.anim.fade_out)
                sharedViews.forEach { addSharedElement(it, it.transitionName) }
                replace(R.id.container, nextFragment)
                addToBackStack(ShowNoteFragment.BACK_STACK_TAG)
                commit()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerArchiveComponent.builder()
                .context(context)
                .appComponent((context.applicationContext as AppComponentProvider).provideAppComponent())
                .build()
                .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notesList.layoutManager = LinearLayoutManager(context)
        binding.notesList.adapter = adapter
        binding.notesList.addItemDecoration(NotesItemDecoration(8f.toPx(resources),
                8f.toPx(resources),
                16f.toPx(resources),
                16f.toPx(resources)))

        archiveViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}
