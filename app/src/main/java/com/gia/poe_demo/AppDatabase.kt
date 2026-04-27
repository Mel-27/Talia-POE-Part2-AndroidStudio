package com.gia.poe_demo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ BudgetGoal::class, Reminder::class],
    version = 2,
    exportSchema = false
)
// This class sets up the Room database for the app and provides access to all DAOs.
// It also ensures only one instance of the database is used throughout the app.


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

abstract class AppDatabase : RoomDatabase()
{

   // abstract fun expenseDao(): ExpenseDao
    abstract fun budgetGoalDao(): BudgetGoalDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_bee_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}