package com.gia.poe_demo

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    suspend fun getAllExpenses(): List<Expense>
}