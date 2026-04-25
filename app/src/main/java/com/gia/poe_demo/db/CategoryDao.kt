package com.gia.poe_demo.db

import androidx.lifecycle.LiveData
import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.gia.poe_demo.data.entities.Category

/**
 * CategoryDao - Data Access Object for Category CRUD operations.
 * All suspend functions run on background threads via coroutines.
 * Reference: IIE PROG7313 Module Manual (2026); Android Room Docs
 */

@Dao
interface CategoryDao {

    /** Inserts a new category; returns the new row ID */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    /** Updates an existing category */
    @Update
    suspend fun update(category: Category)

    /** Deletes a category */
    @Delete
    suspend fun delete(category: Category)

    /** Observes all categories ordered alphabetically - used by UI list */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllLive(): LiveData<List<Category>>

    /** One-shot fetch for spinner/adapter population */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    suspend fun getAll(): List<Category>

    /** Fetch single category by ID */
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun  getById(id: Long): Category?
}