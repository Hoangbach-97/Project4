package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    private var isError = false

    fun setShouldReturnError(value: Boolean) {
        isError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (isError) {
            return Result.Error("Reminders Error")
        }
        return try {
            Result.Success(reminders)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (isError) {
            return Result.Error("Reminder Error")
        }
        return try {
            val reminder = reminders.find {
                it.id == id
            }
            if (reminder != null) {
                Result.Success(reminder)
            } else {
                Result.Error("Not found!")
            }
        } catch (e: Exception) {
            Result.Error("Reminder Error")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}
