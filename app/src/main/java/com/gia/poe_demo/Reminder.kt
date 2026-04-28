package com.gia.poe_demo

import androidx.room.Entity
import androidx.room.PrimaryKey

// This data class represents a payment or bill reminder,
// including the amount, due date, and notification settings.
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,           // e.g. "Netflix", "Rent"
    val amount: Double,         // e.g. 199.00
    val dueDateMillis: Long,    // stored as epoch ms for easy Calendar math
    val dueDateDisplay: String, // e.g. "15 Apr 2026" (shown in UI)
    val notifyDaysBefore: Int   // 0 = on day, 1, 3, or 5
)
