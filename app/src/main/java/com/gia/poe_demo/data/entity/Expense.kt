package com.gia.poe_demo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val categoryId: Long = 0,
    val description: String,
    val amount: Double,
    val date: Long,
    val startTime: String = "",
    val endTime: String = "",
    val receiptPhotoPath: String? = null,
    val receiptUri: String? = null
)