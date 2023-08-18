package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var isError = false

    fun setShouldReturnError(value: Boolean) {
        isError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (isError) {
            return Result.Error("Not found")
        }
        reminders?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            ("Not found")
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (isError) {
            Result.Error("Not found")
        }
        reminders?.find { it.id == id }?.let {
            Result.Success(it)
        }
        return Result.Error(
            "Not found"
        )
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}
