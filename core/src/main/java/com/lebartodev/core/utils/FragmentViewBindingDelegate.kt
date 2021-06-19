package com.lebartodev.core.utils

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline initializer: (LayoutInflater) -> T) = FragmentViewBindingDelegate(initializer, this)
class FragmentViewBindingDelegate<T : ViewBinding>(
        private val initializer: (LayoutInflater) -> T,
        private val fragment: Fragment
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null
    private val lifecycleObserver = BindingLifecycleObserver()

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        this.binding?.let { return it }
        thisRef.viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        binding = initializer(thisRef.layoutInflater)
        return binding!!
    }

    private inner class BindingLifecycleObserver : LifecycleObserver {

        private val mainHandler = Handler(Looper.getMainLooper())

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            mainHandler.post {
                binding = null
            }
        }
    }
}
