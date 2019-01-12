package com.lebartodev.lnote.utils.matcher

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher


object MatcherUtil {
    fun isZeroSize(): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun matchesSafely(target: View): Boolean {
                val pixels = target.height * target.scaleY + target.width * target.scaleX
                return pixels == 0F
            }

            override fun describeTo(description: Description) {
            }
        }
    }
}