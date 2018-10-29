package com.lebartodev.lnote.common.notes

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.LNoteApplicationMock
import com.lebartodev.lnote.utils.di.component.DaggerAppComponentTest
import com.lebartodev.lnote.utils.rule.DisableAnimationsRule
import com.lebartodev.lnote.utils.rule.Repeat
import com.lebartodev.lnote.utils.rule.RepeatRule
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NotesActivityTest {
    @get:Rule
    var repeatRule = RepeatRule()
    @get:Rule
    var rule: ActivityTestRule<NotesActivity> = ActivityTestRule<NotesActivity>(NotesActivity::class.java)
    @get:Rule
    var animationsRule = DisableAnimationsRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val component = DaggerAppComponentTest.builder().withApplication(getApp()).build()
        component.inject(this)
    }

    private fun getApp(): LNoteApplicationMock {
        return InstrumentationRegistry.getInstrumentation()
                .targetContext.applicationContext as LNoteApplicationMock
    }

    @Test
    fun onCreate() {
        onView(withId(R.id.notes_list)).check(matches(isDisplayed()))
    }

    @Test
    @Repeat(3)
    fun createNote() {
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.save_button)).perform(click())
    }

    @Test
    fun onOptionsItemSelected() {
    }
}