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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
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
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [30])
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var appContext : Application

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setUp(){
        appContext = ApplicationProvider.getApplicationContext()
        val reminder1 = ReminderDTO("Test1","test1","Work",20.0,40.0)
        val reminder2 = ReminderDTO("Test2","test2","coffee",25.0,45.0)
        val reminder3 = ReminderDTO("Test3","test3","gym",30.0,50.0)
        val reminder4 = ReminderDTO("Test4","test4","home",35.0,55.0)
        val remindersList = listOf(reminder1,reminder2,reminder3,reminder4)
        dataSource = FakeDataSource(remindersList.toMutableList())
        viewModel = SaveReminderViewModel(appContext,dataSource)

        stopKoin()// stop the original app koin, which is launched when the application starts (in "MyApp")
        val myModule = module {
            viewModel {
                SaveReminderViewModel(
                    appContext,
                    get() as FakeDataSource
                )
            }
            single { get() as FakeDataSource }
        }
        startKoin {
            androidLogger()
            androidContext(appContext)
            modules(listOf(myModule))
        }
    }

    @Test
    fun showToast_reminderSaved_WhenCallSaveReminder(){
        viewModel.saveReminder(
            ReminderDataItem(
                title = "Test",
                description = "test",
                location = "Work",
                latitude = 20.0,
                longitude = 40.0
            )
        )
        val result = viewModel.showToast.getOrAwaitValue()
        assertThat(result,`is`("Reminder Saved !"))
    }

    @Test
    fun navigationCommand_NavigateBack_WhenCallSaveReminder(){
        viewModel.saveReminder(
            ReminderDataItem(
                title = "Test",
                description = "test",
                location = "Work",
                latitude = 20.0,
                longitude = 40.0
            )
        )
        val result = viewModel.navigationCommand.getOrAwaitValue()
        assertThat(result,`is`(NavigationCommand.Back))
    }

    @Test
    fun showLoading_StatesChanges_WhenSaveReminder(){
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder( ReminderDataItem(
            title = "Test",
            description = "test",
            location = "Work",
            latitude = 20.0,
            longitude = 40.0
        ))
        assertThat(viewModel.showLoading.getOrAwaitValue(), Matchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), Matchers.`is`(false))
    }

    @Test
    fun validateReminderTitle_ShowSnackBarInt_WhenValidateCalled(){
        viewModel.validateEnteredData( ReminderDataItem(
            title = null,
            description = "test",
            location = "work",
            latitude = 20.0,
            longitude = 40.0
        ))

        val result = viewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result,`is`(R.string.err_enter_title))
    }

    @Test
    fun validateReminderDescription_ShowSnackBarInt_WhenValidateCalled(){
        viewModel.validateEnteredData( ReminderDataItem(
            title = "Test",
            description = null,
            location = "work",
            latitude = 20.0,
            longitude = 40.0
        ))

        val result = viewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result,`is`(R.string.err_enter_description))
    }

    fun validateReminderLocation_ShowSnackBarInt_WhenValidateCalled(){
        viewModel.validateEnteredData( ReminderDataItem(
            title = "Test",
            description = "test",
            location = null,
            latitude = 20.0,
            longitude = 40.0
        ))

        val result = viewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(result,`is`(R.string.err_select_location))
    }

    @Test
    fun testOnClear_ReminderValuesISNull_WhenOnClearCalled(){
        viewModel.onClear()
        assertThat(viewModel.reminderTitle.getOrAwaitValue(),`is`(nullValue()))
        assertThat(viewModel.reminderDescription.getOrAwaitValue(),`is`(nullValue()))
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValue(),`is`(nullValue()))
        assertThat(viewModel.longitude.getOrAwaitValue(),`is`(nullValue()))
        assertThat(viewModel.latitude.getOrAwaitValue(),`is`(nullValue()))
        assertThat(viewModel.selectedPOI.getOrAwaitValue(),`is`(nullValue()))
    }



    @After
    fun stopKoinAfterTest(){
        stopKoin()
    }
}