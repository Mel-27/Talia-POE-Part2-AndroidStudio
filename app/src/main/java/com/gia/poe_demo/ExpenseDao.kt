package com.gia.poe_demo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    suspend fun getAllExpenses(): List<Expense>
}