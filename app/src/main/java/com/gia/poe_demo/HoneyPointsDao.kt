package com.gia.poe_demo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface HoneyPointsDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(honeyPoints: HoneyPoints)


    @Query("SELECT * FROM honey_points WHERE userId = :userId")
    suspend fun getPointsForUser(userId: Int): HoneyPoints?


    @Query("UPDATE honey_points SET points = points + :amount WHERE userId = :userId")
    suspend fun addPoints(userId: Int, amount: Int)


    @Query("UPDATE honey_points SET points = MAX(0, points - :amount) WHERE userId = :userId")
    suspend fun deductPoints(userId: Int, amount: Int)


    @Query("UPDATE honey_points SET streakCount = :streak, lastLogDate = :date WHERE userId = :userId")
    suspend fun updateStreak(userId: Int, streak: Int, date: String)
}