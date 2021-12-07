package com.lebartodev.core.di.utils

import android.content.Context
import com.lebartodev.core.di.app.CoreComponent

interface CoreComponentProvider {
    val coreComponent: CoreComponent
}

fun Context.coreComponent(): CoreComponent =
    (applicationContext as CoreComponentProvider).coreComponent