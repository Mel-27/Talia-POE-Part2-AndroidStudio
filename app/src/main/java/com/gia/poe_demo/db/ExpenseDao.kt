package com.gia.poe_demo.db

import androidx.lifecycle.LiveData
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.gia.poe_demo.data.entities.Expense

/**
 * Data class for category total query results.
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    /** All expenses newest first - LiveData for real-time UI updates*/
    @Query("SELECT * FROM expenses ORDER BY date DESC, createdAt DESC")
    fun getAllLive(): LiveData<List<Expense>>

    /** Total amount spent per category in a period - used for category totals screen */
    @Query("""
        SELECT categoryId, SUM(amount) AS total
        FROM expenses
        WHERE date BETWEEN :start AND :end
        GROUP BY categoryId
    """)
    fun getCategoryTotals(start: Long, end: Long): LiveData<List<CategoryTotal>>

    /** Sum of all expense amounts in a period */
    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :start AND :end")
    suspend fun getTotal(start: Long, end: Long): Double?

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Expense?
}