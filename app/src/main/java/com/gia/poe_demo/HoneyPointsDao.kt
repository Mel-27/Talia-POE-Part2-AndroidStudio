package com.gia.poe_demo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entities.HoneyPoints

/*
Handles reading and updating points for the logged-in user.
Adapted from (Android Developers,2019)
 */

@Dao
interface HoneyPointsDao {

    @Query("SELECT * FROM honey_points LIMIT 1")
    suspend fun getPoints(): HoneyPoints?


    @Query("SELECT * FROM honey_points WHERE userId = :userId LIMIT 1")
    suspend fun getPointsForUser(userId: Int): HoneyPoints?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(points: HoneyPoints)

    @Update
    suspend fun update(points: HoneyPoints)

    @Query("DELETE FROM honey_points")
    suspend fun clear()


}
/*
References:
Android Developers. (2019). Accessing data using Room DAOs  |
Android Developers. [online]
Available at: https://developer.android.com/training/data-storage/room/accessing-data.

 */