package com.lebartodev.lnote.utils.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionValues
import com.lebartodev.lnote.R

private const val PROPERTY_CORNER_RADIUS = "PROPERTY_CORNER_RADIUS"
private const val PROPERTY_ELEVATION = "PROPERTY_ELEVATION"

class CardExpandTransition : ChangeBounds {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getTransitionProperties(): Array<String> {
        return arrayOf(PROPERTY_CORNER_RADIUS, PROPERTY_ELEVATION)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        if (transitionValues.view is CardView) {
            transitionValues.values[PROPERTY_ELEVATION] = transitionValues.view.elevation
            transitionValues.values[PROPERTY_CORNER_RADIUS] = (transitionValues.view as CardView).radius
        } else {
            transitionValues.values[PROPERTY_ELEVATION] = 0f
            transitionValues.values[PROPERTY_CORNER_RADIUS] = 0f
        }
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        if (transitionValues.view is CardView) {
            transitionValues.values[PROPERTY_ELEVATION] = transitionValues.view.elevation
            transitionValues.values[PROPERTY_CORNER_RADIUS] = (transitionValues.view as CardView).radius
        } else {
            transitionValues.values[PROPERTY_ELEVATION] = 0f
            transitionValues.values[PROPERTY_CORNER_RADIUS] = 0f
        }

    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        val changeBounds = super.createAnimator(sceneRoot, startValues, endValues)
        if (startValues == null || endValues == null)
            return null

        val startElevation = startValues.values[PROPERTY_ELEVATION] as Float
        val endElevation = endValues.values[PROPERTY_ELEVATION] as Float

        if (startElevation != 0f) {
            endValues.view.elevation = startElevation
        } else {
            startValues.view.elevation = endElevation
        }

        val elevation = ObjectAnimator.ofFloat(endValues.view, "elevation", endElevation)

        val transition = AnimatorSet()
        transition.playTogether(changeBounds, elevation)
        transition.duration = sceneRoot.resources.getInteger(R.integer.animation_duration).toLong()
        return transition
    }
}