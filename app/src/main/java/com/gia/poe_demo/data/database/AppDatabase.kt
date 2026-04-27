package com.gia.poe_demo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gia.poe_demo.data.dao.UserDao
import com.gia.poe_demo.data.entity.User
import com.gia.poe_demo.data.entity.Expense
import com.gia.poe_demo.data.dao.ExpenseDao

// @Database tells Room this is the main database class, listing all entities and the version number
// exportSchema = false just means we're not saving the schema to a file
// ref: https://developer.android.com/training/data-storage/room#database
// ref: https://developer.android.com/reference/androidx/room/Database
@Database(entities = [User::class, Expense::class], version = 2, exportSchema = false)
// abstract class extending RoomDatabase so Room can generate the implementation at compile time
// ref: https://developer.android.com/reference/androidx/room/RoomDatabase
abstract class AppDatabase : RoomDatabase() {

    // abstract function that gives access to the UserDao
    // ref: https://developer.android.com/training/data-storage/room/accessing-data
    abstract fun userDao(): UserDao

    // abstract function that gives access to the ExpenseDao
    // ref: https://developer.android.com/training/data-storage/room/accessing-data
    abstract fun expenseDao(): ExpenseDao

    companion object {
        // @Volatile makes sure INSTANCE is always up to date across all threads
        // ref: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-volatile/
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // singleton pattern so only one instance of the database gets created
        // synchronized block prevents multiple threads from creating it at the same time
        // ref: https://developer.android.com/training/data-storage/room#database
        // ref: https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // building the Room database with the application context and database name
                // ref: https://developer.android.com/reference/androidx/room/Room#databaseBuilder(android.content.Context,java.lang.Class,java.lang.String)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budgetbee_database"
                )
                    // fallbackToDestructiveMigration wipes and recreates the DB on version upgrade
                    // ref: https://developer.android.com/reference/androidx/room/RoomDatabase.Builder#fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // getInstance() kept as an alias so existing code using it does not break
        // ref: https://developer.android.com/training/data-storage/room#database
        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}

/*
References:

Android Developers, 2024. Save data in a local database using Room.
Available at: https://developer.android.com/training/data-storage/room#database
[Accessed 20 April 2026].

Android Developers, 2024. Database.
Available at: https://developer.android.com/reference/androidx/room/Database
[Accessed 20 April 2026].

Android Developers, 2024. RoomDatabase.
Available at: https://developer.android.com/reference/androidx/room/RoomDatabase
[Accessed 20 April 2026].

Android Developers, 2024. Access data using Room DAOs.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
[Accessed 20 April 2026].

Android Developers, 2024. Room - databaseBuilder.
Available at: https://developer.android.com/reference/androidx/room/Room#databaseBuilder(android.content.Context,java.lang.Class,java.lang.String)
[Accessed 20 April 2026].

Android Developers, 2024. RoomDatabase.Builder - fallbackToDestructiveMigration.
Available at: https://developer.android.com/reference/androidx/room/RoomDatabase.Builder#fallbackToDestructiveMigration()
[Accessed 20 April 2026].

Kotlin, 2024. Volatile.
Available at: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-volatile/
[Accessed 20 April 2026].

Kotlin, 2024. Shared Mutable State and Concurrency.
Available at: https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html
[Accessed 20 April 2026].
*/