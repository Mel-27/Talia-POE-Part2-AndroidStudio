package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
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
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)

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

            // basic field validation before doing anything with the database
            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                if (fullName.isEmpty()) findViewById<TextInputLayout>(R.id.tilFullName).error = "Required"
                if (email.isEmpty()) tilEmail.error = "Required"
                if (username.isEmpty()) tilUsername.error = "Required"
                if (password.isEmpty()) tilPassword.error = "Required"
                return@setOnClickListener
            }

            // checking that both password fields match before registering
            if (password != confirmPassword) {
                tilConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

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
                    password = password
                ))

                // logging all users to Logcat to verify data is saving correctly
                // ref: https://developer.android.com/reference/android/util/Log
                val allUsers = userDao.getAllUsers()
                android.util.Log.d("RoomDB", "Users in database: $allUsers")

                // saving registration state to SharedPreferences so the app remembers the user
                // ref: https://developer.android.com/training/data-storage/shared-preferences
                val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                prefs.edit().putBoolean("isRegistered", true).apply()

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