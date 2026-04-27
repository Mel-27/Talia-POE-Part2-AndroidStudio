package com.gia.poe_demo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entity.User

// @Dao marks this interface as a Room Database Access Object
// ref: https://developer.android.com/training/data-storage/room/accessing-data
// ref: https://developer.android.com/reference/androidx/room/Dao
@Dao
interface UserDao {

    // @Insert tells Room to handle the SQL insert automatically
    // suspend means it runs off the main thread using coroutines
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#insert
    // ref: https://developer.android.com/reference/androidx/room/Insert
    @Insert
    suspend fun registerUser(user: User)

    // queries the users table by username or email and verifies the password
    // LIMIT 1 so it only returns one result
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#query
    // ref: https://developer.android.com/reference/androidx/room/Query
    @Query("SELECT * FROM users WHERE (username = :input OR email = :input) AND password = :password LIMIT 1")
    suspend fun loginUser(input: String, password: String): User?

    // checks if a username already exists before registering
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#query
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // checks if an email already exists before registering
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#query
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // grabs all users from the database, used for Logcat debugging to verify data is saving correctly
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#query
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // @Update tells Room to handle the SQL update automatically using the primary key
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#update
    // ref: https://developer.android.com/reference/androidx/room/Update
    @Update
    suspend fun updateUser(user: User)

    // @Delete tells Room to handle the SQL delete automatically using the primary key
    // ref: https://developer.android.com/training/data-storage/room/accessing-data#delete
    // ref: https://developer.android.com/reference/androidx/room/Delete
    @Delete
    suspend fun deleteUser(user: User)
}

/*
References:

Android Developers, 2024. Access data using Room DAOs.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
[Accessed 21 April 2026].

Android Developers, 2024. Dao.
Available at: https://developer.android.com/reference/androidx/room/Dao
[Accessed 21 April 2026].

Android Developers, 2024. Insert.
Available at: https://developer.android.com/reference/androidx/room/Insert
[Accessed 19 April 2026].

Android Developers, 2024. Query.
Available at: https://developer.android.com/reference/androidx/room/Query
[Accessed 19 April 2026].

Android Developers, 2024. Update.
Available at: https://developer.android.com/reference/androidx/room/Update
[Accessed 19 April 2026].

Android Developers, 2024. Delete.
Available at: https://developer.android.com/reference/androidx/room/Delete
[Accessed 18 April 2026].

Android Developers, 2024. Kotlin coroutines on Android.
Available at: https://developer.android.com/kotlin/coroutines
[Accessed 20 April 2026].
*/