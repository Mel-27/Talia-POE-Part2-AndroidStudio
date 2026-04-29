package com.gia.poe_demo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.gia.poe_demo.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val currencyFormat = NumberFormat.getNumberInstance(Locale("en", "ZA"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        db = AppDatabase.getInstance(this)

        loadData()
        setupButton()
    }

    private fun loadData() {
        lifecycleScope.launch {

            val now = System.currentTimeMillis()

            val start = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            val expenses = withContext(Dispatchers.IO) {
                db.expenseDao().getByPeriod(start, now).first()
            }

            val total = expenses.sumOf { it.amount }

            val budget = 5000.0
            val remaining = budget - total

            val percent = ((total / budget) * 100).toInt().coerceIn(0, 100)

            findViewById<TextView>(R.id.tvTotalBudget).text =
                "R ${currencyFormat.format(budget)}"

            findViewById<TextView>(R.id.tvBudgetSubtitle).text =
                "R ${currencyFormat.format(total)} spent · R ${currencyFormat.format(remaining)} left"

            findViewById<ProgressBar>(R.id.progressTotalBudget).progress = percent

            val groceries = total * 0.3
            val entertainment = total * 0.2
            val transport = total * 0.15
            val food = total * 0.2
            val health = total * 0.15

            updateBar(R.id.barGroceries, groceries)
            updateBar(R.id.barEntertainment, entertainment)
            updateBar(R.id.barTransport, transport)
            updateBar(R.id.barFood, food)
            updateBar(R.id.barHealth, health)

            updateLabel(R.id.barLabelGroceries, groceries)
            updateLabel(R.id.barLabelEntertainment, entertainment)
            updateLabel(R.id.barLabelTransport, transport)
            updateLabel(R.id.barLabelFood, food)
            updateLabel(R.id.barLabelHealth, health)
        }
    }

    private fun updateBar(id: Int, value: Double) {
        val bar = findViewById<View>(id)
        val height = (value / 1000 * 150).toInt()
        bar.layoutParams.height = height.coerceAtLeast(20)
        bar.requestLayout()
    }

    private fun updateLabel(id: Int, value: Double) {
        findViewById<TextView>(id).text = "R${value.toInt()}"
    }

    private fun setupButton() {
        findViewById<MaterialButton>(R.id.btnUpdateMonthlyBudget).setOnClickListener {
            val value = findViewById<EditText>(R.id.etMonthlyBudget)
                .text.toString().toDoubleOrNull()

            if (value == null) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            }
        }
    }
}