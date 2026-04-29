package com.gia.poe_demo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun registerUser(user: User)


    @Query("SELECT * FROM users WHERE fullName = :fullName AND passwordHash = :passwordHash LIMIT 1")
    suspend fun loginUserByHash(fullName: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE fullName = :fullName AND password = :password LIMIT 1")
    suspend fun loginUserByPlain(fullName: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    suspend fun loginUserByEmailHash(email: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginUserByEmailPlain(email: String, password: String): User?

    // KEEP ALL YOUR EXISTING METHODS
    @Query("SELECT * FROM users WHERE fullName = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}