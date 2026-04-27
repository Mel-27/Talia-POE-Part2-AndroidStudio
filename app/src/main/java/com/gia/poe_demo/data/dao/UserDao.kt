package com.gia.poe_demo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gia.poe_demo.data.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE (username = :input OR email = :input) AND password = :password LIMIT 1")
    suspend fun loginUser(input: String, password: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}