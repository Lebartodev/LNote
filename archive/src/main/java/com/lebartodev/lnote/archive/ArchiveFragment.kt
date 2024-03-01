package com.lebartodev.lnote.archive

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.di.utils.CoreComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.archive.databinding.FragmentArchiveBinding
import com.lebartodev.lnote.archive.di.DaggerArchiveComponent
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.toPx


class ArchiveFragment : BaseFragment() {
    private val binding by viewBinding(FragmentArchiveBinding::inflate)
    override val fragmentView: View = binding.root

    private val archiveViewModel: ArchiveViewModel by viewModels { viewModelFactory }

    private val adapter = ArchiveAdapter(
        deleteListener = { it.id?.let { id -> archiveViewModel.deleteNote(id) } },
        restoreListener = { it.id?.let { id -> archiveViewModel.restoreNote(id) } }
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerArchiveComponent.builder()
            .coreComponent((context.applicationContext as CoreComponentProvider).coreComponent)
            .build()
            .inject(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.archiveToolbar) {
            setNavigationIcon(R.drawable.ic_arrow_white)
            setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        }
        with(binding.notesList) {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
            addItemDecoration(
                PaddingDecoration(
                    8f.toPx(resources),
                    8f.toPx(resources),
                    16f.toPx(resources),
                    16f.toPx(resources)
                )
            )
        }
        archiveViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}
