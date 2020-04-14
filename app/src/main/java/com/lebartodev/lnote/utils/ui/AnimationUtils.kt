package com.lebartodev.lnote.utils.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.lebartodev.lnote.R
import kotlin.math.sqrt

object AnimationUtils {
    fun registerRevealAnimation(context: Context, view: View, revealSettings: RevealAnimationSetting, startColor: Int, endColor: Int, finishListener: () -> Unit) {
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                view.removeOnLayoutChangeListener(this)
                val cx: Int = revealSettings.centerX
                val cy: Int = revealSettings.centerY
                val width: Int = revealSettings.width
                val height: Int = revealSettings.height

                val finalRadius = sqrt(width * width + height * height.toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                anim.duration = context.resources.getInteger(R.integer.animation_duration).toLong()
                anim.interpolator = FastOutSlowInInterpolator()
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        finishListener()
                    }
                })
                anim.start()
                startBackgroundColorAnimation(view, startColor, endColor, context.resources.getInteger(R.integer.animation_duration))
            }

        })
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCircularRevealExitAnimation(context: Context, view: View, revealSettings: RevealAnimationSetting, startColor: Int, endColor: Int, finishListener: () -> Unit) {
        val cx = revealSettings.centerX
        val cy = revealSettings.centerY
        val width = revealSettings.width
        val height = revealSettings.height
        val initRadius = sqrt(width * width + height * height.toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f)
        anim.duration = context.resources.getInteger(R.integer.animation_duration).toLong()
        anim.interpolator = FastOutSlowInInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) { //Important: This will prevent the view's flashing (visible between the finished animation and the Fragment remove)
                view.visibility = View.GONE
                finishListener()
            }
        })
        anim.start()
        startBackgroundColorAnimation(view, startColor, endColor, context.resources.getInteger(R.integer.animation_duration))
    }

    private fun startBackgroundColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.duration = duration.toLong()
        anim.addUpdateListener { valueAnimator -> view.setBackgroundColor((valueAnimator.animatedValue as Int)) }
        anim.start()
    }

    data class RevealAnimationSetting(
            val centerX: Int,
            val centerY: Int,
            val width: Int,
            val height: Int)
}





