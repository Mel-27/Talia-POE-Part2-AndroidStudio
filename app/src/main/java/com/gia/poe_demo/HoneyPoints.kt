package com.gia.poe_demo.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "honey_points")
data class HoneyPoints(

    @PrimaryKey
    val userId: Int,

    val points: Int = 0
)