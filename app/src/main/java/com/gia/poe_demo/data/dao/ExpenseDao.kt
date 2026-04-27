package com.gia.poe_demo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gia.poe_demo.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

   @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC")
    suspend fun getExpensesByUser(userId: Int): List<Expense>
}