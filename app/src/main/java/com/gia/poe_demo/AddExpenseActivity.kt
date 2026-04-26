package com.gia.poe_demo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etDescription  = findViewById<TextInputEditText>(R.id.etDescription)
        val etAmount       = findViewById<TextInputEditText>(R.id.etAmount)
        val etDate         = findViewById<TextInputEditText>(R.id.etDate)
        val tilDescription = findViewById<TextInputLayout>(R.id.tilDescription)
        val tilAmount      = findViewById<TextInputLayout>(R.id.tilAmount)
        val userId = intent.getIntExtra("USER_ID", -1)
        val db             = AppDatabase.getInstance(this)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            etDate.setText(sdf.format(Date(selection)))
        }

        etDate.setOnClickListener {
            if (!datePicker.isAdded)
                datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnSaveExpense).setOnClickListener {
            val description = etDescription.text.toString().trim()
            val amount      = etAmount.text.toString().trim()
            val date        = etDate.text.toString().trim()

            tilDescription.error = if (description.isEmpty()) "Enter a description" else null
            tilAmount.error      = if (amount.isEmpty() || amount.toDoubleOrNull() == null) "Enter a valid amount" else null

            if (description.isEmpty() || amount.toDoubleOrNull() == null || date.isEmpty()) {
                if (date.isEmpty()) Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                db.expenseDao().insert(
                    Expense(
                        userId = userId,
                        description = description,
                        amount      = amount.toDouble(),
                        date        = date
                    )
                )
                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}

//References:
//https://www.geeksforgeeks.org/kotlin/material-design-date-picker-in-android-using-kotlin/
//https://developer.android.com/topic/libraries/architecture/coroutines
//https://medium.com/android-ideas/findviewbyid-in-kotlin-ce4d22193c79