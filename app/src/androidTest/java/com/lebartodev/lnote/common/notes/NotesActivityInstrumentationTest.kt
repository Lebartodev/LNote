package com.lebartodev.lnote.common.notes

import android.widget.DatePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.lnote.R
import com.lebartodev.lnote.di.notes.NotesModule
import com.lebartodev.lnote.utils.RecyclerViewMatcher.Companion.withRecyclerView
import com.lebartodev.lnote.utils.ViewActionUtil
import com.lebartodev.lnote.utils.actions.ClickCloseIconAction
import com.lebartodev.lnote.utils.di.app.AppComponentTest
import com.lebartodev.lnote.utils.matcher.MatcherUtil.isZeroSize
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import com.lebartodev.lnote.utils.rule.DisableAnimationRule
import com.lebartodev.lnote.utils.rule.Repeat
import com.lebartodev.lnote.utils.rule.RepeatRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NotesActivityInstrumentationTest {
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
    @Repeat(1)
    fun createNote() {
        onView(withId(R.id.text_title)).check(matches(not(hasFocus())))
        onView(withId(R.id.text_description)).check(matches(not(hasFocus())))
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_title)).check(matches(hasFocus()))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_description)).check(matches(hasFocus()))
        onView(withId(R.id.save_button)).perform(click())
        onView(withId(R.id.notes_list)).check(matches(hasMinimumChildCount(1)))
        onView(withRecyclerView(R.id.notes_list).atPosition(0))
                .check(matches(hasDescendant(withText("Title"))))
        onView(withRecyclerView(R.id.notes_list).atPosition(0))
                .check(matches(hasDescendant(withText("Description"))))
    }

    @Test
    @Repeat(1)
    fun createEmptyNote() {
        onView(withId(R.id.text_title)).check(matches(not(hasFocus())))
        onView(withId(R.id.text_description)).check(matches(not(hasFocus())))
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.save_button)).perform(click())
        onView(withText(R.string.error_note_create)).inRoot(
                withDecorView(not(rule.activity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    @Repeat(1)
    fun createNoteWithDate() {
        onView(withId(R.id.text_title)).check(matches(not(hasFocus())))
        onView(withId(R.id.text_description)).check(matches(not(hasFocus())))
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_title)).check(matches(hasFocus()))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_description)).check(matches(hasFocus()))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.note_date_chip)).check(matches(withText("Sun, 05 May 2019")))

        onView(withId(R.id.save_button)).perform(click())
        onView(withId(R.id.notes_list)).check(matches(hasMinimumChildCount(1)))
        onView(withRecyclerView(R.id.notes_list).atPosition(0))
                .check(matches(hasDescendant(withText("Title"))))
        onView(withRecyclerView(R.id.notes_list).atPosition(0))
                .check(matches(hasDescendant(withText("Description"))))
        onView(withRecyclerView(R.id.notes_list).atPosition(0))
                .check(matches(hasDescendant(withText("Sun, 05 May 2019"))))
    }

    @Test
    fun openBottomBarAdd() {
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.fab_add)).check(matches(isZeroSize()))
        onView(withId(R.id.notes_list)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
    }

    @Test
    fun titleDependency() {
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.fab_add)).check(matches(isZeroSize()))

        onView(withId(R.id.text_description)).perform(click(), typeText("Description"))
        onView(withId(R.id.text_description)).check(matches(hasFocus()))
        onView(withId(R.id.text_title)).check(matches(withHint("Description")))


        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_title)).check(matches(withText("Title")))

        val testString = "Test test test test test test test test test test test"
        onView(withId(R.id.text_title)).perform(click(), clearText())
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText(testString))
        onView(withId(R.id.text_title)).check(matches(withHint(testString.substring(0, 24))))
    }

    @Test
    fun openDateDialog() {
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(ViewActionUtil.touchDownAndUp(0F, 0F))
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(allOf(withId(R.id.date_chip), withParent(withId(R.id.bottom_sheet_add)))).check(matches(withText("Sun, 05 May 2019")))
        onView(allOf(withId(R.id.date_chip), withParent(withId(R.id.bottom_sheet_add)))).perform(ClickCloseIconAction())
        onView(allOf(withId(R.id.date_chip), withParent(withId(R.id.bottom_sheet_add)))).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun deleteDraftedNote() {
        onView(withId(R.id.text_title)).check(matches(not(hasFocus())))
        onView(withId(R.id.text_description)).check(matches(not(hasFocus())))
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_title)).check(matches(hasFocus()))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_description)).check(matches(hasFocus()))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.delete_button)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText(R.string.note_deleted)))
        onView(withId(R.id.text_title)).check(matches(withText("")))
        onView(withId(R.id.text_description)).check(matches(withText("")))
        onView(withId(R.id.date_chip)).check(matches(not(isDisplayed())))
    }

    @Test
    fun restoreDraftedNote() {
        onView(withId(R.id.text_title)).check(matches(not(hasFocus())))
        onView(withId(R.id.text_description)).check(matches(not(hasFocus())))
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_title)).check(matches(hasFocus()))
        onView(withId(R.id.text_description)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_description)).check(matches(hasFocus()))
        onView(withId(R.id.fab_more)).perform(click())
        onView(withId(R.id.calendar_button)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.delete_button)).perform(click())
        onView((withId(com.google.android.material.R.id.snackbar_action))).perform(click());
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.text_title)).check(matches(withText("Title")))
        onView(withId(R.id.text_description)).check(matches(withText("Description")))
        onView(withId(R.id.date_chip)).check(matches(withText("Sun, 05 May 2019")))
    }


}