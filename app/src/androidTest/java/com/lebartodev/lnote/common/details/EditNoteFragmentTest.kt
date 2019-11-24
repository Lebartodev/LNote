package com.lebartodev.lnote.common.details

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.di.app.AppComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import com.lebartodev.lnote.utils.rule.DisableAnimationRule
import com.lebartodev.lnote.utils.rule.RepeatRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditNoteFragmentTest {
    @get:Rule
    var repeatRule = RepeatRule()
    @get:Rule
    var rule: ActivityTestRule<NotesActivity> = ActivityTestRule<NotesActivity>(NotesActivity::class.java, false, false)
    @get:Rule
    var animationsRule = DisableAnimationRule()

    @Before
    fun setUp() {
        (getApp().component() as AppComponentTest)
                .plus(NotesModule())
                .inject(this)

        rule.launchActivity(null)
        rule.activity.supportFragmentManager.beginTransaction()

    }

    private fun getApp(): LNoteApplicationMock {
        return InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext as LNoteApplicationMock
    }

    @Test
    fun openCreationDetails() {
        onView(ViewMatchers.withId(R.id.fab_add)).perform(click())
        onView(ViewMatchers.withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(ViewMatchers.withId(R.id.full_screen_button)).perform(click())
        onView(ViewMatchers.withId(R.id.bottom_sheet_add_expanded)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

}