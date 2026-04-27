package com.gia.poe_demo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity tells Room this data class represents a table in the database
// tableName sets the name of the table to "expenses"
// ref: https://developer.android.com/training/data-storage/room/defining-data
@Entity(tableName = "expenses")
// data class used here because Room needs to be able to compare and copy instances
// ref: https://kotlinlang.org/docs/data-classes.html
data class Expense(
    // @PrimaryKey with autoGenerate = true means Room assigns a unique ID automatically
    // ref: https://developer.android.com/reference/androidx/room/PrimaryKey
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // foreign key linking the expense to a specific user
    val userId: Int,

    // optional category ID for grouping expenses
    val categoryId: Long = 0,

    // description of the expense
    val description: String,

    // amount stored as a Double to support decimal values
    val amount: Double,

    // date stored as a Long timestamp in milliseconds
    // ref: https://developer.android.com/reference/java/lang/System#currentTimeMillis()
    val date: Long,

    // optional start and end times for the expense
    val startTime: String = "",
    val endTime: String = "",

    // optional receipt photo stored as a file path or URI string
    val receiptPhotoPath: String? = null,
    val receiptUri: String? = null
)

/*
References:

Android Developers, 2024. Define data using Room entities.
Available at: https://developer.android.com/training/data-storage/room/defining-data
[Accessed 20 April 2026].

Android Developers, 2024. PrimaryKey.
Available at: https://developer.android.com/reference/androidx/room/PrimaryKey
[Accessed 21 April 2026].

Android Developers, 2024. Entity.
Available at: https://developer.android.com/reference/androidx/room/Entity
[Accessed 21 April 2026].

Kotlin, 2024. Data Classes.
Available at: https://kotlinlang.org/docs/data-classes.html
[Accessed 20 April 2026].

Android Developers, 2024. System - currentTimeMillis.
Available at: https://developer.android.com/reference/java/lang/System#currentTimeMillis()
[Accessed 23 April 2026].
*/