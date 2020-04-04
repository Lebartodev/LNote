package com.lebartodev.lnote.common.details

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.lebartodev.lnote.R
import com.lebartodev.lnote.common.notes.NotesActivity
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.RecyclerViewMatcher
import com.lebartodev.lnote.utils.di.app.NotesComponentTest
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import com.lebartodev.lnote.utils.rule.DisableAnimationRule
import com.lebartodev.lnote.utils.rule.RepeatRule
import org.hamcrest.Matchers
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
        (getApp().component() as NotesComponentTest)
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
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.full_screen_button)).perform(click())
        onView(withId(R.id.bottom_sheet_add_expanded)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun closeCreationDetails() {
        openCreationDetails()
        onView(withId(R.id.full_screen_button)).perform(click())
        onView(withId(R.id.bottom_sheet_add_expanded)).check(doesNotExist())
    }

    @Test
    fun sameCreationDetailsContent() {
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.full_screen_button)).perform(click())
        onView(withId(R.id.text_title)).check(matches(withText("Title")))
        onView(withId(R.id.text_description)).check(matches(withText("Description")))
        onView(withId(R.id.date_chip)).check(matches(withText("Sun, 05 May 2019")))


    }

    @Test
    fun openDetails() {
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed())).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed())).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())

        onView(withId(R.id.save_button)).perform(click())
        onView(RecyclerViewMatcher.withRecyclerView(R.id.notes_list).atPosition(0)).perform(click())

        onView(withId(R.id.text_title)).check(matches(withText("Title")))
        onView(withId(R.id.text_description)).check(matches(withText("Description")))
        onView(withId(R.id.date_chip)).check(matches(withText("Sun, 05 May 2019")))
    }

    @Test
    fun deleteDraftedNote() {
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.full_screen_button)).perform(click())
        onView(withId(R.id.delete_button)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.note_deleted)))
        onView(withId(R.id.text_title)).check(matches(withText("")))
        onView(withId(R.id.text_description)).check(matches(withText("")))
    }

    @Test
    fun restoreDraftedNote() {
        onView(withId(R.id.fab_add)).perform(click())
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.full_screen_button)).perform(click())
        onView(withId(R.id.delete_button)).perform(click())
        onView((withId(com.google.android.material.R.id.snackbar_action))).perform(click());
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(withText("Title")))
        onView(withId(R.id.text_description)).check(matches(withText("Description")))
    }
}