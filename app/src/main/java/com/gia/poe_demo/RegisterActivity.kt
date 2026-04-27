package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.gia.poe_demo.data.entity.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

// extended AppCompatActivity which is just the standard way Android activities work
// ref: https://developer.android.com/guide/components/activities/intro-activities
class RegisterActivity : AppCompatActivity() {

    // loads the register screen when the activity starts
    // ref: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // grabbing the Room DB instance using the singleton pattern
        // ref: https://developer.android.com/training/data-storage/room
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // used findViewById() to grab all the input fields and text input layouts
        // ref: https://developer.android.com/reference/android/view/View#findViewById(int)
        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val tilFullName = findViewById<TextInputLayout>(R.id.tilFullName)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword)

        // tab click navigates to LoginActivity using an explicit Intent and calls finish()
        // ref: https://developer.android.com/guide/components/intents-filters
        findViewById<android.view.View>(R.id.tabLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<android.view.View>(R.id.btnRegister).setOnClickListener {

            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // reset all errors before revalidating
            tilFullName.error = null
            tilEmail.error = null
            tilUsername.error = null
            tilPassword.error = null
            tilConfirmPassword.error = null

            var isValid = true

            // full name — letters and spaces only, no numbers or special characters
            if (fullName.isEmpty()) {
                tilFullName.error = "Required"
                isValid = false
            } else if (!fullName.matches(Regex("^[a-zA-Z ]+$"))) {
                tilFullName.error = "Full name must contain letters only"
                isValid = false
            }

            // email — must be a valid email format using Android's built-in Patterns class
            // ref: https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
            if (email.isEmpty()) {
                tilEmail.error = "Required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Enter a valid email address"
                isValid = false
            }

            // username — just required, no format restrictions
            if (username.isEmpty()) {
                tilUsername.error = "Required"
                isValid = false
            }

            // password strength validation - min 8 chars, uppercase, number, special character
            if (password.isEmpty()) {
                tilPassword.error = "Required"
                isValid = false
            } else if (password.length < 8) {
                tilPassword.error = "Password must be at least 8 characters"
                isValid = false
            } else if (!password.any { it.isUpperCase() }) {
                tilPassword.error = "Password must contain at least one uppercase letter"
                isValid = false
            } else if (!password.any { it.isDigit() }) {
                tilPassword.error = "Password must contain at least one number"
                isValid = false
            } else if (!password.any { !it.isLetterOrDigit() }) {
                tilPassword.error = "Password must contain at least one special character"
                isValid = false
            }

            // confirm password - must match password field
            if (confirmPassword.isEmpty()) {
                tilConfirmPassword.error = "Required"
                isValid = false
            } else if (password != confirmPassword) {
                tilConfirmPassword.error = "Passwords do not match"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            // all validation passed - proceed with database operations
            // used lifecycleScope.launch to run all Room DB operations off the main thread
            // ref: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
            lifecycleScope.launch {

                // checking for duplicate username and email before inserting
                // ref: https://developer.android.com/training/data-storage/room/accessing-data
                val existingUsername = userDao.getUserByUsername(username)
                val existingEmail = userDao.getUserByEmail(email)

                if (existingUsername != null) {
                    tilUsername.error = "Username already taken"
                    return@launch
                }
                if (existingEmail != null) {
                    tilEmail.error = "Email already registered"
                    return@launch
                }

                // inserting the new user into Room DB using the User entity
                // ref: https://developer.android.com/training/data-storage/room/defining-data
                userDao.registerUser(User(
                    fullName = fullName,
                    email = email,
                    username = username,
                    password = HashUtils.md5(password)
                ))

                // logging all users to Logcat to verify data is saving correctly
                // ref: https://developer.android.com/reference/android/util/Log
                val allUsers = userDao.getAllUsers()
                android.util.Log.d("RoomDB", "Users in database: $allUsers")

                // saving registration state to SharedPreferences so the app remembers the user
                // ref: https://developer.android.com/training/data-storage/shared-preferences
                val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                prefs.edit()
                    .putBoolean("isRegistered", true)
                    .putString("loggedInUsername", username)
                    .apply()

                // navigating to MainActivity and calling finish() so the user cant go back to register
                // ref: https://developer.android.com/reference/android/app/Activity#finish()
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }
        }

        // tvLogin click also navigates back to LoginActivity
        // ref: https://developer.android.com/guide/components/intents-filters
        findViewById<android.view.View>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}