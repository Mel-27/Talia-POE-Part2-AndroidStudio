package com.gia.poe_demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.gia.poe_demo.data.entities.Expense
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private var selectedDateMillis: Long = 0L
    private var id: Long = -1
    private var selectedCategoryId: Long = -1

    // Store file URI (image or PDF)
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val actvCategory = findViewById<AutoCompleteTextView>(R.id.actvCategory)

        val tilDescription = findViewById<TextInputLayout>(R.id.tilDescription)
        val tilAmount = findViewById<TextInputLayout>(R.id.tilAmount)

        val db = AppDatabase.getInstance(this)


        val appPrefs = getSharedPreferences("APP", MODE_PRIVATE)
        val budgetPrefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)

        id = appPrefs.getLong("USER_ID", -1)

        if (id == -1L) {
            val username = budgetPrefs.getString("loggedInUsername", null)

            if (username != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val user = db.userDao().getUserByUsername(username)

                    if (user != null) {
                        id = user.id
                        appPrefs.edit().putLong("USER_ID", id).apply()
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddExpenseActivity, "Please log in again", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@AddExpenseActivity, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
        }

        lifecycleScope.launch {
            val categories = db.categoryDao().getAll()

            withContext(Dispatchers.Main) {
                if (categories.isEmpty()) {
                    Toast.makeText(
                        this@AddExpenseActivity,
                        "No categories found. Please add categories first.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@withContext
                }

                val names = categories.map { "${it.iconEmoji} ${it.name}" }

                val adapter = ArrayAdapter(
                    this@AddExpenseActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    names
                )

                actvCategory.setAdapter(adapter)

                actvCategory.setOnClickListener {
                    actvCategory.showDropDown()
                }

                actvCategory.setOnItemClickListener { _, _, position, _ ->
                    selectedCategoryId = categories[position].id
                }
            }
        }


        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDateMillis = selection
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            etDate.setText(sdf.format(Date(selection)))
        }

        etDate.setOnClickListener {
            if (!datePicker.isAdded) {
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }
        }


        val filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    selectedFileUri = uri

                    // Persist permission (important for future access)
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    Toast.makeText(this, "File selected!", Toast.LENGTH_SHORT).show()
                }
            }

        findViewById<LinearLayout>(R.id.photoUploadArea).setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))

        }


        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        // Replace your btnSaveExpense click listener:
        // In your btnSaveExpense click listener, change this line:
       // val expenseId = db.expenseDao().insertExpense(expense)  // ✅ Use insertExpense

// Full corrected save block:
        findViewById<MaterialButton>(R.id.btnSaveExpense).setOnClickListener {
            val description = etDescription.text.toString().trim()
            val amount = etAmount.text.toString().trim().toDoubleOrNull()

            tilDescription.error = if (description.isEmpty()) "Enter description" else null
            tilAmount.error = if (amount == null) "Enter valid amount" else null

            if (description.isEmpty() || amount == null ||
                selectedDateMillis == 0L || selectedCategoryId == -1L) {
                Toast.makeText(this, "Complete all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val expense = Expense(
                    categoryId = selectedCategoryId,
                    description = description,
                    amount = amount,
                    date = selectedDateMillis,
                    startTime = "",
                    endTime = "",
                    receiptPhotoPath = selectedFileUri?.toString() ?: ""
                )

                val expenseId = db.expenseDao().insertExpense(expense)

                Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@AddExpenseActivity, ExpensesListActivity::class.java).apply {
                    putExtra("SHOW_NEW_EXPENSE", expenseId)
                    putExtra("NEW_EXPENSE_DATE", selectedDateMillis)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}