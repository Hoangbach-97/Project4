package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.nullValue
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [30])
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var applicationContext: Application

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext()
        val reminder1 = ReminderDTO("title1", "des1", "location1", 10.0, 10.0)
        val reminder2 = ReminderDTO("title2", "des2", "location2", 10.0, 10.0)
        val remindersList = listOf(reminder1, reminder2)
        fakeDataSource = FakeDataSource(remindersList.toMutableList())
        saveReminderViewModel = SaveReminderViewModel(applicationContext, fakeDataSource)

        stopKoin()
        val myModule = module {
            viewModel {
                SaveReminderViewModel(
                    applicationContext,
                    get() as FakeDataSource
                )
            }
            single { get() as FakeDataSource }
        }
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(listOf(myModule))
        }
    }

    @Test
    fun showToast_reminderSaved_WhenCallSaveReminder() {
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                title = "title",
                description = "des",
                location = "location",
                latitude = 10.0,
                longitude = 10.0
            )
        )
        val result = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(result, `is`("Reminder Saved !"))
    }

    @Test
    fun navigationCommand_NavigateBack_WhenCallSaveReminder() {
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                title = "title",
                description = "des",
                location = "location",
                latitude = 10.0,
                longitude = 10.0
            )
        )
        val result = saveReminderViewModel.navigationCommand.getOrAwaitValue()
        assertThat(result, `is`(NavigationCommand.Back))
    }

    @Test
    fun showLoading_StatesChanges_WhenSaveReminder() {
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).pauseDispatcher()
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                title = "title",
                description = "des",
                location = "location",
                latitude = 10.0,
                longitude = 10.0
            )
        )
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))
        (mainCoroutineRule.coroutineContext[ContinuationInterceptor]!! as DelayController).resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))
    }

    @Test
    fun validateReminderTitle_ShowSnackBarInt_WhenValidateCalled() {
        saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                title = null,
                description = "des",
                location = "location",
                latitude = 10.0,
                longitude = 10.0
            )
        )

        val result = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateReminderDescription_ShowSnackBarInt_WhenValidateCalled() {
        saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                title = "title",
                description = null,
                location = "location",
                latitude = 10.0,
                longitude = 10.0
            )
        )

        val result = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result, `is`(R.string.err_enter_description))
    }

    fun validateReminderLocation_ShowSnackBarInt_WhenValidateCalled() {
        saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                title = "title",
                description = "des",
                location = null,
                latitude = 10.0,
                longitude = 10.0
            )
        )

        val result = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result, `is`(R.string.err_select_location))
    }

    @Test
    fun testOnClear_ReminderValuesISNull_WhenOnClearCalled() {
        saveReminderViewModel.onClear()
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue(), `is`(nullValue()))
        assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            `is`(nullValue())
        )
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue(), `is`(nullValue()))
        assertThat(saveReminderViewModel.selectedPOI.getOrAwaitValue(), `is`(nullValue()))
    }

    @After
    fun stopKoinAfterTest() {
        stopKoin()
    }
}