package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun setUp() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun clearUp() {
        remindersDatabase.close()
    }

    @Test
    fun saveAndGetReminder() = runTest {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        remindersDatabase.reminderDao().saveReminder(reminder)
        val result = remindersDatabase.reminderDao().getReminderById(reminder.id)

        assertThat(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveAndDeleteReminder() = runTest {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        remindersDatabase.reminderDao().saveReminder(reminder)
        remindersDatabase.reminderDao().deleteAllReminders()
        val result = remindersDatabase.reminderDao().getReminders()
        assertThat(result.size, `is`(0))
    }

    @Test
    fun saveAndGetAllReminders() = runTest {
        val reminder = ReminderDTO(
            title = "title",
            description = "description",
            location = "location",
            latitude = 10.0,
            longitude = 10.0,
            id = "1"
        )
        val reminder2 = ReminderDTO(
            title = "title2",
            description = "description2",
            location = "location2",
            latitude = 10.0,
            longitude = 10.0,
            id = "2"
        )
        remindersDatabase.reminderDao().saveReminder(reminder)
        remindersDatabase.reminderDao().saveReminder(reminder2)
        val result = remindersDatabase.reminderDao().getReminders()
        assertThat(result.size, `is`(2))
    }
}
