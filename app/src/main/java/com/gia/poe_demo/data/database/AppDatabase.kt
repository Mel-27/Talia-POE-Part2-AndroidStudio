package com.gia.poe_demo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gia.poe_demo.data.dao.UserDao
import com.gia.poe_demo.data.entity.User

// @Database tells Room this is the main database class, listing all entities and the version number
// exportSchema = false just means we're not saving the schema to a file
// ref: https://developer.android.com/training/data-storage/room#database
@Database(entities = [User::class], version = 1, exportSchema = false)
// abstract class extending RoomDatabase so Room can generate the implementation at compile time
// ref: https://developer.android.com/reference/androidx/room/RoomDatabase
abstract class AppDatabase : RoomDatabase() {

    // abstract function that gives access to the UserDao
    // ref: https://developer.android.com/training/data-storage/room/accessing-data
    abstract fun userDao(): UserDao

    companion object {
        // @Volatile makes sure INSTANCE is always up to date across all threads
        // ref: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-volatile/
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // singleton pattern so only one instance of the database gets created
        // synchronized block prevents multiple threads from creating it at the same time
        // ref: https://developer.android.com/training/data-storage/room#database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // building the Room database with the application context and database name
                // ref: https://developer.android.com/reference/androidx/room/Room#databaseBuilder(android.content.Context,java.lang.Class,java.lang.String)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budgetbee_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}