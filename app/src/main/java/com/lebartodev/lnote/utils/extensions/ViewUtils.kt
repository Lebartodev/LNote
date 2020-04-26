package com.lebartodev.lnote.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.lebartodev.lnote.R
import com.lebartodev.lnote.utils.ui.DateChip


fun View.onLayout(listener: (() -> Unit)) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            listener.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun View.animateSlideBottomVisibility(visible: Boolean) {
    Log.d("Lebartodev", "$visible  $visibility $translationY");
    if (visible && visibility == View.VISIBLE || !visible && (visibility == View.GONE || visibility == View.INVISIBLE)) return
    val sceneRoot: ViewGroup = parent as ViewGroup

    this.animate().cancel()
    val startTranslationY = if (!visible) translationY else this.translationY + sceneRoot.height
    val endTranslation = if (visible) translationY else this.translationY + sceneRoot.height

    if (this is DateChip)
        Log.d("Lebartodev", "$visible  $startTranslationY $endTranslation");


    translationY = startTranslationY

    val anim = this.animate().translationY(endTranslation)
    anim.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            if (visible)
                visibility = View.VISIBLE
            if (this@animateSlideBottomVisibility is DateChip)
                Log.d("Lebartodev", "onAnimationStart $visibility $translationY $visible  $startTranslationY $endTranslation");
        }

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            if (!visible)
                visibility = View.GONE
            else {
                visibility = View.VISIBLE
            }
            if (this@animateSlideBottomVisibility is DateChip)
                Log.d("Lebartodev", "onAnimationEnd $visibility $translationY $visible  $startTranslationY $endTranslation");
        }
    })
    anim.interpolator = LinearOutSlowInInterpolator()
    anim.duration = resources.getInteger(R.integer.animation_duration).toLong()
    anim.start()
}

fun View.animateSlideTopVisibility(visible: Boolean) {
    if (visible && visibility == View.VISIBLE || !visible && visibility == View.GONE) return
    val sceneRoot: ViewGroup = parent as ViewGroup
    translationY = if (!visible) 0f else this.translationY - sceneRoot.height
    this.animate().cancel()
    val anim = this.animate().translationY(if (visible) 0f else this.translationY - sceneRoot.height)
    anim.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            if (visible)
                visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            if (!visible)
                visibility = View.GONE
        }
    })
    anim.start()
}