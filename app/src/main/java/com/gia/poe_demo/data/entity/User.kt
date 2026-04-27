package com.gia.poe_demo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity tells Room this is a database table called "users"
// ref: https://developer.android.com/training/data-storage/room/defining-data
@Entity(tableName = "users")
// data class used here so Kotlin auto-generates equals, hashCode and toString
// ref: https://kotlinlang.org/docs/data-classes.html
data class User(
    // @PrimaryKey with autoGenerate = true so Room automatically assigns a unique ID to each user
    // ref: https://developer.android.com/reference/androidx/room/PrimaryKey
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val username: String,
    val password: String
)