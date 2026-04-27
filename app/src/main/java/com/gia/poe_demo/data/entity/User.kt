package com.gia.poe_demo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity tells Room this is a database table called "users"
// ref: https://developer.android.com/training/data-storage/room/defining-data
// ref: https://developer.android.com/reference/androidx/room/Entity
@Entity(tableName = "users")
// data class used here so Kotlin auto-generates equals, hashCode and toString
// ref: https://kotlinlang.org/docs/data-classes.html
data class User(
    // @PrimaryKey with autoGenerate = true so Room automatically assigns a unique ID to each user
    // ref: https://developer.android.com/reference/androidx/room/PrimaryKey
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // stores the users full name
    val fullName: String,

    // stores the users email address
    val email: String,

    // stores the users chosen username
    val username: String,

    // stores the users MD5 hashed password
    // ref: https://developer.android.com/reference/java/security/MessageDigest
    val password: String
)

/*
References:

Android Developers, 2024. Define data using Room entities.
Available at: https://developer.android.com/training/data-storage/room/defining-data
[Accessed 20 April 2026].

Android Developers, 2024. Entity.
Available at: https://developer.android.com/reference/androidx/room/Entity
[Accessed 20 April 2026].

Android Developers, 2024. PrimaryKey.
Available at: https://developer.android.com/reference/androidx/room/PrimaryKey
[Accessed 20 April 2026].

Kotlin, 2024. Data Classes.
Available at: https://kotlinlang.org/docs/data-classes.html
[Accessed 20 April 2026].

Android Developers, 2024. MessageDigest.
Available at: https://developer.android.com/reference/java/security/MessageDigest
[Accessed 20 April 2026].
*/