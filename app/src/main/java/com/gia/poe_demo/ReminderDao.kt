package com.gia.poe_demo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// This DAO manages reminder-related database actions,
// including adding, deleting, and fetching upcoming reminders.
@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY dueDateMillis ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE dueDateMillis >= :fromMillis ORDER BY dueDateMillis ASC")
    fun getUpcomingReminders(fromMillis: Long): Flow<List<Reminder>>
}
