package com.gia.poe_demo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gia.poe_demo.data.entities.Category

/**
 * CategoryDao - Data Access Object for Category CRUD operations.
 * Reference: IIE PROG7313 Module Manual (2026); Android Room Docs
 */

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Category?

}