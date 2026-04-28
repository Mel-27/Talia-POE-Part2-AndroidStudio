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

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByPeriod(startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalForPeriod(startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT categoryId, SUM(amount) as total FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getCategoryTotalsForPeriod(startDate: Long, endDate: Long): LiveData<List<CategoryTotal>>

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteExpense(expenseId: Long)
}

