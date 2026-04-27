package com.gia.poe_demo

import android.os.Bundle
import android.util.Log
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

//Activity responsible for adding a new expense entry.
class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Set the layout for this activity
        setContentView(R.layout.activity_add_expense)

        //Link UI components to variables using findViewById
        //Adapted from (Medium, 2018 - findViewById in Kotlin.)
        val etDescription  = findViewById<TextInputEditText>(R.id.etDescription)
        val etAmount       = findViewById<TextInputEditText>(R.id.etAmount)
        val etDate         = findViewById<TextInputEditText>(R.id.etDate)
        val tilDescription = findViewById<TextInputLayout>(R.id.tilDescription)
        val tilAmount      = findViewById<TextInputLayout>(R.id.tilAmount)

        //Retrieve user ID passed from previous activity
        val userId = intent.getIntExtra("USER_ID", -1)

        //Get instance of the Room database
        val db             = AppDatabase.getInstance(this)

        // Create a Material Date Picker instance
        //Adapted from (GeekforGeeks, 2022 - Material Design Date Picker in Android using Kotlin)
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")//Title shown on picker dialog
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) //Default day = today
            .build()

        //Handle date selection
        datePicker.addOnPositiveButtonClickListener { selection ->
            etDate.tag = selection

            //Format selected date into readable format
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            etDate.setText(sdf.format(Date(selection)))
        }

        //Show date picker when date field is clicked
        etDate.setOnClickListener {
            if (!datePicker.isAdded)
                datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        //Back button - closes activity
        //Adapted from (Medium, 2018 - findViewById in Kotlin.)
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        //Cancel button - also closes activity
        //Adapted from (Medium, 2018 - findViewById in Kotlin.)
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }

        //Save button logic
        //Adapted from (Medium, 2018 - findViewById in Kotlin.)
        findViewById<MaterialButton>(R.id.btnSaveExpense).setOnClickListener {

            //Get user input values
            val description = etDescription.text.toString().trim()
            val amount      = etAmount.text.toString().trim()
            val date        = etDate.text.toString().trim()

            //Input validation for description and amount
            tilDescription.error = if (description.isEmpty()) "Enter a description" else null
            tilAmount.error      = if (amount.isEmpty() || amount.toDoubleOrNull() == null) "Enter a valid amount" else null

            //Check if any field is invalid.
            if (description.isEmpty() || amount.toDoubleOrNull() == null || date.isEmpty()) {

                //Show toast if date is missing.
                if (date.isEmpty()) Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Launch coroutine to perform database operation off the main thread
            //Adapted from (Android Developers , 2026 - Use Kotlin coroutines with lifecycle-aware components)
            lifecycleScope.launch {

                // Retrieve stored date value or fallback to current time
                val date = etDate.tag as? Long ?: System.currentTimeMillis()

                // Insert new expense into database
                db.expenseDao().insert(
                    Expense(
                        userId = userId,
                        description = description,
                        amount      = amount.toDouble(),
                        date        = date
                    )
                )


                val prefs  = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                val userId = prefs.getInt("loggedInUserId", -1)
                if (userId != -1) {
                    val existing = db.honeyPointsDao().getPointsForUser(userId)
                    if (existing == null) {
                        db.honeyPointsDao().upsert(HoneyPoints(userId = userId, points = 5))
                    } else {
                        db.honeyPointsDao().addPoints(userId, GamificationManager.POINTS_ADD_EXPENSE)
                    }
                    Log.d("AddExpense", "Awarded ${GamificationManager.POINTS_ADD_EXPENSE} Honey Points to userId=$userId")
                }
                // Notify user of success
                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
                // Close activity after saving
                finish()
            }
        }
    }
}


/*

References:

GeeksforGeeks, 2022. Material Design Date Picker in Android using Kotlin.
Available at: https://www.geeksforgeeks.org/kotlin/material-design-date-picker-in-android-using-kotlin/
[Accessed 27 April 2026].

Android Developers, 2026. Use Kotlin coroutines with lifecycle-aware components.
Available at: https://developer.android.com/topic/libraries/architecture/coroutines
[Accessed 27 April 2026].

Android Ideas (Medium), 2018. findViewById in Kotlin.
Available at: https://medium.com/android-ideas/findviewbyid-in-kotlin-ce4d22193c79
[Accessed 27 April 2026].

 */