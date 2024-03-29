package com.lebartodev.lnote.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.lebartodev.lnote.utils.R


fun View.onLayout(listener: (() -> Unit)) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            listener.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun View.animateSlideBottomVisibility(visible: Boolean) {
    val alreadyVisible = visible && visibility == View.VISIBLE
    val alreadyInvisible = !visible && (visibility == View.GONE || visibility == View.INVISIBLE)
    if (alreadyVisible || alreadyInvisible) return
    val sceneRoot: ViewGroup = parent as ViewGroup

    this.animate().cancel()
    val startTranslationY = if (!visible) 0f else sceneRoot.height.toFloat()
    val endTranslation = if (visible) 0f else sceneRoot.height.toFloat()

    translationY = startTranslationY
    val anim = this.animate().translationY(endTranslation)
    anim.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            if (visible)
                visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            if (!visible)
                visibility = View.GONE
        }
    })
    anim.interpolator = if (visible) LinearOutSlowInInterpolator() else AccelerateInterpolator()
    anim.duration = resources.getInteger(R.integer.animation_duration).toLong()
    anim.start()
}

fun View.animateSlideTopVisibility(visible: Boolean) {
    val alreadyVisible = visible && visibility == View.VISIBLE
    val alreadyInvisible = !visible && (visibility == View.GONE || visibility == View.INVISIBLE)
    if (alreadyVisible || alreadyInvisible) return
    val sceneRoot: ViewGroup = parent as ViewGroup

    this.animate().cancel()
    val startTranslationY = if (!visible) 0f else -sceneRoot.height.toFloat()
    val endTranslation = if (visible) 0f else -sceneRoot.height.toFloat()

    translationY = startTranslationY
    val anim = this.animate().translationY(endTranslation)
    anim.setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            if (visible)
                visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            if (!visible)
                visibility = View.GONE
        }
    })
    anim.interpolator = if (visible) LinearOutSlowInInterpolator() else AccelerateInterpolator()
    anim.duration = resources.getInteger(R.integer.animation_duration).toLong()
    anim.start()
}