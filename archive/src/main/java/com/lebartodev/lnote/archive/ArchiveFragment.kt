package com.lebartodev.lnote.archive

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lebartodev.core.base.BaseFragment
import com.lebartodev.core.di.utils.AppComponentProvider
import com.lebartodev.core.utils.viewBinding
import com.lebartodev.lnote.archive.databinding.FragmentArchiveBinding
import com.lebartodev.lnote.archive.di.DaggerArchiveComponent
import com.lebartodev.lnote.utils.ui.PaddingDecoration
import com.lebartodev.lnote.utils.ui.toPx
import javax.inject.Inject


class ArchiveFragment : BaseFragment() {
    private val binding by viewBinding(FragmentArchiveBinding::inflate)

    @Inject
    lateinit var viewModelFactory: ArchiveViewModelFactory
    private val archiveViewModel: ArchiveViewModel by lazy {
        ViewModelProvider(this,
            viewModelFactory)[ArchiveViewModel::class.java]
    }

    private val adapter = ArchiveAdapter(
        deleteListener = {
            it.id?.let { id -> archiveViewModel.deleteNote(id) }
        }, restoreListener = {
        it.id?.let { id -> archiveViewModel.restoreNote(id) }
    })

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerArchiveComponent.builder()
            .context(context)
            .appComponent(
                (context.applicationContext as AppComponentProvider).provideAppComponent())
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

        binding.archiveToolbar.setNavigationIcon(R.drawable.ic_arrow_white)
        binding.archiveToolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        binding.notesList.layoutManager = LinearLayoutManager(context)
        binding.notesList.adapter = adapter
        binding.notesList.addItemDecoration(PaddingDecoration(8f.toPx(resources),
            8f.toPx(resources),
            16f.toPx(resources),
            16f.toPx(resources)))

        archiveViewModel.getNotes().observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}
