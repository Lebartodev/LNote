package com.lebartodev.lnote.common.notes

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lebartodev.lnote.R
import com.lebartodev.lnote.data.entity.Note
import com.lebartodev.lnote.data.entity.ViewModelObject
import com.lebartodev.lnote.utils.LNoteViewModelFactory
import com.lebartodev.lnote.utils.RecyclerViewMatcher.Companion.withRecyclerView
import com.lebartodev.lnote.utils.di.component.AppComponentTest
import com.lebartodev.lnote.utils.matcher.MatcherUtil.isZeroSize
import com.lebartodev.lnote.utils.mocks.LNoteApplicationMock
import com.lebartodev.lnote.utils.mocks.LNoteViewModelFactoryMock
import com.lebartodev.lnote.utils.rule.DisableAnimationRule
import com.lebartodev.lnote.utils.rule.Repeat
import com.lebartodev.lnote.utils.rule.RepeatRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class NotesActivityInstrumentationTest {
    @get:Rule
    var repeatRule = RepeatRule()
    @get:Rule
    var rule: ActivityTestRule<NotesActivity> = ActivityTestRule<NotesActivity>(NotesActivity::class.java, false, false)
    @get:Rule
    var animationsRule = DisableAnimationRule()
    @Inject
    lateinit var viewModelFactory: LNoteViewModelFactory

    private val mockNotesData: MutableLiveData<ViewModelObject<List<Note>>> = MutableLiveData()
    private val mockNotesList = arrayListOf<Note>()

    @Before
    fun setUp() {
        (getApp().component() as AppComponentTest).inject(this)
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.loadNotes()).thenReturn(mockNotesData)
        doAnswer {
            mockNotesList.add(Note(null, it.getArgument(0), System.currentTimeMillis(), it.getArgument(1)))
            mockNotesData.value = ViewModelObject.success(mockNotesList)
            MutableLiveData<Long>()
        }.whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel).saveNote(any(), any())

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
    fun openBottomBarAdd() {
        val bottomAddSheetBehavior = BottomSheetBehavior.from(
                rule.activity.findViewById<ConstraintLayout>(R.id.bottom_sheet_add))
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
        onView(withId(R.id.fab_add)).perform(click())
        assert(bottomAddSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
        onView(withId(R.id.bottom_sheet_add)).perform(swipeUp())
        onView(withId(R.id.fab_add)).check(matches(isZeroSize()))
    }


}