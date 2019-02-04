package com.lebartodev.lnote.common.notes

import android.widget.DatePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers.withDecorView
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
import com.lebartodev.lnote.utils.ViewActionUtil
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
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import java.text.SimpleDateFormat
import java.util.*
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
    private val mockNoteDate: MutableLiveData<Calendar?> = MutableLiveData()
    private val mockCreateNote: MutableLiveData<ViewModelObject<Long>> = MutableLiveData()
    private val mockDescriptionText: MutableLiveData<String?> = MutableLiveData()

    @Before
    fun setUp() {
        (getApp().component() as AppComponentTest).inject(this)
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.loadNotes()).thenReturn(mockNotesData)
        doAnswer {
            mockNotesList.add(
                    Note(null, it.getArgument(0), mockNoteDate.value?.timeInMillis, System.currentTimeMillis(),
                            it.getArgument(1)))
            mockNotesData.value = ViewModelObject.success(mockNotesList)
            mockCreateNote
        }.whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel).saveNote(any(), any())
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.selectedDate).thenReturn(mockNoteDate)
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.setDate(any(), any(), any()))
                .then {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, it.arguments[0] as Int)
                    calendar.set(Calendar.MONTH, it.arguments[1] as Int)
                    calendar.set(Calendar.DAY_OF_MONTH, it.arguments[2] as Int)
                    mockNoteDate.postValue(calendar)
                }

        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.selectedDateString())
                .thenReturn(
                        Transformations.map(mockNoteDate) {
                            if (it == null) {
                                ""
                            } else {
                                val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
                                formatter.format(it.time)
                            }
                        })
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.clearDate()).then {
            mockNoteDate.postValue(null)
        }
        whenever(
                (viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.onDescriptionChanged(anyString())).then {
            val text: String = it.getArgument(0)
            if (text.length > 12)
                mockDescriptionText.postValue(text.substring(0, 12))
            else {
                mockDescriptionText.postValue(text)
            }
        }
        whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel.descriptionTextLiveData).thenReturn(
                mockDescriptionText)
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

        doAnswer {
            mockCreateNote
        }.whenever((viewModelFactory as LNoteViewModelFactoryMock).notesViewModel).saveNote(any(), any())

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
        mockCreateNote.postValue(ViewModelObject.error(NullPointerException(), null))
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
        onView(withId(R.id.date_layout)).perform(ViewActionUtil.touchDownAndUp(0F, 0F))
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.date_text)).check(matches(withText("Sun, 05 May 2019")))

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
        onView(withId(R.id.text_title)).check(matches(withText("Description")))


        onView(withId(R.id.text_title)).perform(click(), clearText(), typeText("Title"))
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("Description"))
        onView(withId(R.id.text_title)).check(matches(withText("Title")))

        onView(withId(R.id.text_title)).perform(click(), clearText())
        onView(withId(R.id.text_description)).perform(click(), clearText(), typeText("12345678901234567"))
        onView(withId(R.id.text_title)).check(matches(withText("123456789012")))
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
        onView(withId(R.id.date_layout)).perform(ViewActionUtil.touchDownAndUp(0F, 0F))
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
                PickerActions.setDate(2019, 5, 5))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.date_text)).check(matches(withText("Sun, 05 May 2019")))
        onView(withId(R.id.date_text)).perform(ViewActionUtil.clickRightDrawable())
        onView(withId(R.id.date_text)).check(matches(withText("")))
    }


}