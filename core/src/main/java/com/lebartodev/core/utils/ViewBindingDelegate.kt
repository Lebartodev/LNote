package com.lebartodev.core.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty

//inline fun <reified T : ViewBinding> ViewGroup.viewBinding(
//        lifecycleAware: Boolean = false,
//): ReadOnlyProperty<ViewGroup, T> {
//    return viewBinding(T::class.java, lifecycleAware)
//}

//fun <T : ViewBinding> ViewGroup.viewBinding(
//        viewBindingClass: Class<T>,
//        lifecycleAware: Boolean = false,
//): ReadOnlyProperty<ViewGroup, T> = (viewBindingClass as BindViewBinding).bind()

//internal class BindViewBinding<out VB : ViewBinding>(viewBindingClass: Class<VB>) {
//
//    private val bindViewBinding = viewBindingClass.getMethod("bind", View::class.java)
//
//    @Suppress("UNCHECKED_CAST")
//    fun bind(view: View): VB {
//        return bindViewBinding(null, view) as VB
//    }
//}