package com.gia.poe_demo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gia.poe_demo.data.entities.Category
import com.gia.poe_demo.data.entities.Expense
import com.gia.poe_demo.db.CategoryDao
import com.gia.poe_demo.db.ExpenseDao

/**
 * AppDatabase — single Room database instance for Budget Bee.
 * Reference: IIE PROG7313 Module Guide (2026)
 */

@Database(
    entities = [Expense::class, Category::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

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
                android.util.Log.d("AppDatabase", "Database instance created")
                instance
            }
        }
    }
}