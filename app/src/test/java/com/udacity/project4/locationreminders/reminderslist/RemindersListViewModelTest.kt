package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [30])
class RemindersListViewModelTest {


    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @After
    fun finish() {
        stopKoin()
    }

    @Test
    fun showNoData_emptyList() {
        //given
        val remindersList = MutableLiveData<List<ReminderDataItem>>()
        //when
        remindersListViewModel.showNoData.value =
            remindersList.value == null || remindersList.value!!.isEmpty()

        //then
        assertTrue(remindersListViewModel.showNoData.getOrAwaitValue())
    }

    @Test
    fun loadReminders_notEmptyList() = runBlocking {
        //given
        val reminder1 = ReminderDTO("title1", "des1", "location1", 10.0, 10.0)
        fakeDataSource.saveReminder(reminder1)

        //when
        remindersListViewModel.loadReminders()

        val result = remindersListViewModel.remindersList.getOrAwaitValue()

        //then
        assertThat(result, `is`(notNullValue()))
    }

    @Test
    fun loadReminders_showError() = runBlocking {
        //given
        val reminder1 = ReminderDTO("title1", "des1", "location1", 10.0, 10.0)
        fakeDataSource.saveReminder(reminder1)

        fakeDataSource.setShouldReturnError(true)

        //when
        remindersListViewModel.loadReminders()

        val result = remindersListViewModel.showSnackBar.getOrAwaitValue()

        //then
        assertThat(result, `is`("Reminders Error"))
    }
}
