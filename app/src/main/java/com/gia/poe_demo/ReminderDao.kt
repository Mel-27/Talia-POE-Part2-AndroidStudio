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

/*
References
-Google (n.d.) Room Persistence Library.
Available at: https://developer.android.com/training/data-storage/room
(Accessed: 27 April 2026).

-Google (n.d.) Android Developers: Activities.
Available at: https://developer.android.com/guide/components/activities/intro-activities
(Accessed: 27 April 2026).

-Google (n.d.) Kotlin Coroutines on Android.
Available at: https://developer.android.com/kotlin/coroutines
(Accessed: 27 April 2026).

-Google (n.d.) View Binding.
Available at: https://developer.android.com/topic/libraries/view-binding
(Accessed: 27 April 2026).

-JetBrains (n.d.) Kotlin Language Documentation.
Available at: https://kotlinlang.org/docs/home.html
(Accessed: 27 April 2026).

-Google (n.d.) Data Access Objects (DAO) in Room.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
(Accessed: 27 April 2026).

-Google (n.d.) Android Lifecycle and LifecycleScope.
Available at: https://developer.android.com/topic/libraries/architecture/lifecycle
(Accessed: 27 April 2026).
 */