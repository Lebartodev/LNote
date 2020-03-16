package com.lebartodev.lnote.common.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.lebartodev.lnote.R
import com.lebartodev.lnote.di.settings.DaggerSettingsComponent
import com.lebartodev.lnote.di.settings.SettingsModule
import javax.inject.Inject

class SettingsBottomView : ConstraintLayout {
    @Inject
    lateinit var viewModelFactory: SettingsViewModelFactory
    private lateinit var notesViewModel: SettingsViewModel
    private val bottomPanelSwitch: Switch


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.view_settings, this)
        bottomPanelSwitch = findViewById(R.id.bottom_panel_switch)
        DaggerSettingsComponent.builder()
                .settingsModule(SettingsModule(context))
                .build()
                .inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        notesViewModel = ViewModelProviders.of(context as FragmentActivity, viewModelFactory)[SettingsViewModel::class.java]

        notesViewModel.bottomPanelEnabled().observe(context as FragmentActivity, Observer {
            bottomPanelSwitch.isChecked = it
        })

        bottomPanelSwitch.setOnCheckedChangeListener { v, b -> notesViewModel.setBottomPanelEnabled(b) }


    }
}