package com.gia.poe_demo.data.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/*
 * Category entity - stores expense categories locally in RoomDB
 * Each category has an emoji icon, name and optional monthly spending limit.
 * Reference: IIE PROG7313 Module Guide (2026); Android Room Docs
 */

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "\uD83D\uDED2", // Emoji used as the visual icon
    val monthlyLimit: Double = 0.0, // optional spending cap
    val createdAt: Long = System.currentTimeMillis()
)