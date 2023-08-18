package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setUp() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository =
            RemindersLocalRepository(remindersDatabase.reminderDao(), Dispatchers.Main)
    }

    @After
    fun clearUp() {
        remindersDatabase.close()
    }

    @Test
    fun saveAndGetReminderById() = runBlocking {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        remindersLocalRepository.saveReminder(reminder)
        val result = remindersLocalRepository.getReminder(reminder.id)

        result as Result.Success
        assertThat(result.data.id, `is`(reminder.id))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun saveAndDeleteReminder() = runBlocking {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        remindersLocalRepository.saveReminder(reminder)

        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminders()
        result as Result.Success
        assertThat(result.data.count(), `is`(0))

    }

    @Test
    fun saveAndGetAllReminders() = runBlocking {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        val reminder1 = ReminderDTO(
            title = "title1",
            description = "description1",
            location = "location1",
            latitude = 10.0,
            longitude = 10.0,
            id = "2"
        )
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.saveReminder(reminder1)
        val result = remindersLocalRepository.getReminders()
        result as Result.Success
        assertThat(result.data.count(), `is`(2))

    }

    @Test
    fun saveAndTryToGetAnotherReminder() = runBlocking {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )

        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder("2")
        result as Result.Error
        assertThat(result.message, `is`("Not found!"))

    }
}