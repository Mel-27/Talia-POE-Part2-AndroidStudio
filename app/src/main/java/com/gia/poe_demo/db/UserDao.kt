package com.gia.poe_demo.db
/**
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entities.User

/**
 * Data Access Object for User authentication and profile operations.
 * Reference: IIE PROG7313 Module Guide (2026)
 */

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE (username = :identifier OR email = :identifier) AND passwordHash = :passwordHash LIMIT 1")
    suspend fun login(identifier: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("UPDATE users SET honeyPoints = :points WHERE id = :userId")
    suspend fun updateHoneyPoints(userId: Long, points: Int)

    @Query("UPDATE users SET streak = :streak, lastLoginDate = :date WHERE id = :userId")
    suspend fun updateStreak(userId: Long, streak: Int, date: Long)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)
}
        **/