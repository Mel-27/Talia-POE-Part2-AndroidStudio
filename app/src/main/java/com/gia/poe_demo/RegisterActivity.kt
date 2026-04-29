package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.gia.poe_demo.data.entities.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        // ref: https://developer.android.com/reference/android/app/Activity#finish()
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

            // full name — letters and spaces only using Kotlin Regex
            // ref: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/
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
            // used lifecycleScope.launch with Dispatchers.IO to run all Room DB operations off the main thread
            // ref: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
            // ref: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
            lifecycleScope.launch(Dispatchers.IO) {

                // checking for duplicate username and email before inserting
                // ref: https://developer.android.com/training/data-storage/room/accessing-data
                val existingUsername = userDao.getUserByUsername(username)
                val existingEmail = userDao.getUserByEmail(email)

                if (existingUsername != null) {
                    withContext(Dispatchers.Main) {
                        tilUsername.error = "Username already taken"
                    }
                    return@launch
                }
                if (existingEmail != null) {
                    withContext(Dispatchers.Main) {
                        tilEmail.error = "Email already registered"
                    }
                    return@launch
                }

                // inserting the new user into Room DB using the User entity
                // ref: https://developer.android.com/training/data-storage/room/defining-data
                userDao.registerUser(User(
                    fullName = fullName,
                    email = email,
                    password = password,
                    passwordHash = HashUtils.md5(password)
                    // hashing the password using MD5 before storing
                    // ref: https://developer.android.com/reference/java/security/MessageDigest

                ))

                // In RegisterActivity.kt, after userDao.registerUser()
                lifecycleScope.launch(Dispatchers.IO) {
                    val insertedUsers = userDao.getAllUsers()
                    android.util.Log.d("RegisterDebug", "ALL USERS AFTER INSERT: $insertedUsers")

                    // Verify the specific user was saved
                    val newUser = userDao.getUserByUsername(username)
                    android.util.Log.d("RegisterDebug", "NEW USER: $newUser")
                    android.util.Log.d("RegisterDebug", "Stored password: ${newUser?.password}")
                    android.util.Log.d("RegisterDebug", "Stored hash: ${newUser?.passwordHash}")
                }

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

                // switching back to main thread to navigate
                // ref: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
                withContext(Dispatchers.Main) {
                    // navigating to MainActivity and calling finish() so the user cant go back to register
                    // ref: https://developer.android.com/reference/android/app/Activity#finish()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        // tvLogin click also navigates back to LoginActivity
        // ref: https://developer.android.com/guide/components/intents-filters
        // ref: https://developer.android.com/reference/android/app/Activity#finish()
        findViewById<android.view.View>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

/*
References:

Android Developers, 2024. Introduction to Activities.
Available at: https://developer.android.com/guide/components/activities/intro-activities
[Accessed 20 April 2026].

Android Developers, 2024. The Activity Lifecycle.
Available at: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
[Accessed 20 April 2026].

Android Developers, 2024. Save data in a local database using Room.
Available at: https://developer.android.com/training/data-storage/room
[Accessed 20 April 2026].

Android Developers, 2024. View - findViewById.
Available at: https://developer.android.com/reference/android/view/View#findViewById(int)
[Accessed 20 April 2026].

Android Developers, 2024. Intents and Intent Filters.
Available at: https://developer.android.com/guide/components/intents-filters
[Accessed 20 April 2026].

Android Developers, 2024. Activity - finish.
Available at: https://developer.android.com/reference/android/app/Activity#finish()
[Accessed 20 April 2026].

Android Developers, 2024. Patterns - EMAIL_ADDRESS.
Available at: https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
[Accessed 20 April 2026].

Kotlin, 2024. Regex.
Available at: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/
[Accessed 20 April 2026].

Android Developers, 2024. Use Kotlin coroutines with lifecycle-aware components.
Available at: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
[Accessed 20 April 2026].

Android Developers, 2024. Improve app performance with Kotlin coroutines.
Available at: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
[Accessed 20 April 2026].

Android Developers, 2024. Access data using Room DAOs.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
[Accessed 20 April 2026].

Android Developers, 2024. Define data using Room entities.
Available at: https://developer.android.com/training/data-storage/room/defining-data
[Accessed 20 April 2026].

Android Developers, 2024. MessageDigest.
Available at: https://developer.android.com/reference/java/security/MessageDigest
[Accessed 20 April 2026].

Android Developers, 2024. Log.
Available at: https://developer.android.com/reference/android/util/Log
[Accessed 20 April 2026].

Android Developers, 2024. Save key-value data with SharedPreferences.
Available at: https://developer.android.com/training/data-storage/shared-preferences
[Accessed 20 April 2026].
*/