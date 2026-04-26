package com.gia.poe_demo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_goals")
data class BudgetGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val monthYear: String,          // e.g. "2026-03"
    val totalMonthlyBudget: Double, // overall monthly budget (R 5,000)
    val minMonthlyGoal: Double,     // minimum spend goal
    val maxMonthlyGoal: Double,     // maximum spend goal / cap
    val groceriesLimit: Double = 1000.0,
    val entertainmentLimit: Double = 500.0,
    val transportLimit: Double = 1500.0,
    val foodLimit: Double = 1000.0,
    val healthLimit: Double = 500.0
)
