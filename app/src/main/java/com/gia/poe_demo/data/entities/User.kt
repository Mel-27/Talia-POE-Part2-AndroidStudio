package com.gia.poe_demo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity - stores account credentials locally.
 * Passwords are hashed with SHA-256 before storage.
 * Reference: IIE PROG7313 Module Guide (2026)
 */

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val passwordHash: String, // SHA-256 hash of raw password
    val honeyPoints: Int = 0,
    val streak: Int = 0,
    val lastLoginDate: Long = 0L,
    val joinedAt: Long = System.currentTimeMillis()
)
