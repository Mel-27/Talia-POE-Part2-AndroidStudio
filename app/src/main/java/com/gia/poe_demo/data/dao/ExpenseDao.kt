package com.gia.poe_demo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gia.poe_demo.data.entity.Expense

// @Dao marks this interface as a Room Database Access Object
// ref: https://developer.android.com/training/data-storage/room/accessing-data
// ref: https://developer.android.com/reference/androidx/room/Dao
@Dao
interface ExpenseDao {

    // @Insert tells Room to handle the SQL insert automatically
    // suspend means it runs off the main thread using coroutines
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#insert
    // ref: https://developer.android.com/reference/androidx/room/Insert
    @Insert
    suspend fun insert(expense: Expense)

    // queries the expenses table by userId and returns results ordered by newest first
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#query
    // ref: https://developer.android.com/reference/androidx/room/Query
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY id DESC")
    suspend fun getExpensesByUser(userId: Int): List<Expense>
}

/*
References:

Android Developers, 2024. Access data using Room DAOs.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
[Accessed 22 April 2026].

Android Developers, 2024. Dao.
Available at: https://developer.android.com/reference/androidx/room/Dao
[Accessed 22 April 2026].

Android Developers, 2024. Insert.
Available at: https://developer.android.com/reference/androidx/room/Insert
[Accessed 22 April 2026].

Android Developers, 2024. Query.
Available at: https://developer.android.com/reference/androidx/room/Query
[Accessed 22 April 2026].

Android Developers, 2024. Kotlin coroutines on Android.
Available at: https://developer.android.com/kotlin/coroutines
[Accessed 22 April 2026].
*/