package com.lebartodev.lnote.archive.di

import com.lebartodev.core.di.app.CoreComponent
import com.lebartodev.core.di.utils.FeatureScope
import com.lebartodev.lnote.archive.ArchiveFragment
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class], modules = [ArchiveModule::class])
interface ArchiveComponent {
    fun inject(view: ArchiveFragment)
}