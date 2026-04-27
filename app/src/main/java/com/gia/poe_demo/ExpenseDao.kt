package com.gia.poe_demo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

   @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC")
    suspend fun getExpensesByUser(userId: Int): List<Expense>
}

/*
References:
Android Developers. (2019). Accessing data using Room DAOs  |
Android Developers. [online]
Available at: https://developer.android.com/training/data-storage/room/accessing-data.

 */