package com.gia.poe_demo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entities.Expense

/**
 * CategoryTotal — projection for category spending totals query.
 */

data class CategoryTotal(
    val categoryId: Long,
    val total: Double
)

/**
 * ExpenseDao - Data Access Object for Expense CRUD and query operations.
 * Supports date-range filtering for the user-selectable period feature.
 * Reference: IIE PROG7313 Module Manual (2026); Android Room Docs
 */

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY id DESC")
    suspend fun getExpensesByPeriod(start: String, end: String): List<Expense>

}