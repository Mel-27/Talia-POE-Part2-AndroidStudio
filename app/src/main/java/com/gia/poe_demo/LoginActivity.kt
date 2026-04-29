package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// extended AppCompatActivity which is just the standard way Android activities work
// ref: https://developer.android.com/guide/components/activities/intro-activities
class LoginActivity : AppCompatActivity() {

    // loads the login screen when the activity starts
    // ref: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // used findViewById() to grab the input fields and text input layouts
        // ref: https://developer.android.com/reference/android/view/View#findViewById(int)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val btnLogin = findViewById<android.view.View>(R.id.btnLogin)
        val tabSignUp = findViewById<android.view.View>(R.id.tabSignUp)
        val tvRegister = findViewById<android.view.View>(R.id.tvRegister)

        // grabbing the Room DB instance using the singleton pattern
        // ref: https://developer.android.com/training/data-storage/room
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // tab click navigates to RegisterActivity using an explicit Intent
        // ref: https://developer.android.com/guide/components/intents-filters
        tabSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val input = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // reset all errors before revalidating
            tilUsername.error = null
            tilPassword.error = null

            var isValid = true

            // username or email - required, and if it contains @ it must be a valid email format
            // ref: https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
            if (input.isEmpty()) {
                tilUsername.error = "Required"
                isValid = false
            } else if (input.contains("@") && !Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                tilUsername.error = "Incorrect username or email"
                isValid = false
            }

            // password - required
            if (password.isEmpty()) {
                tilPassword.error = "Required"
                isValid = false
            } else if (password.length < 8) {
                tilPassword.error = "Incorrect password"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            // used lifecycleScope.launch with Dispatchers.IO to run the Room DB query off the main thread
            // ref: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
            // ref: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
            lifecycleScope.launch(Dispatchers.IO) {
                // try all combinations of username/fullName/email with hash/plain
                // ref: https://developer.android.com/training/data-storage/room/accessing-data
                var user = userDao.loginUserByHash(input, HashUtils.md5(password))
                if (user == null) user = userDao.loginUserByPlain(input, password)
                if (user == null) user = userDao.loginUserByEmailHash(input, HashUtils.md5(password))
                if (user == null) user = userDao.loginUserByEmailPlain(input, password)
                if (user == null) user = userDao.loginUserByUsernameHash(input, HashUtils.md5(password))
                if (user == null) user = userDao.loginUserByUsernamePlain(input, password)

                // logging login attempt to Logcat for debugging
                // ref: https://developer.android.com/reference/android/util/Log
                Log.d("LoginDebug", "Input: '$input'")
                Log.d("LoginDebug", "Hash: '${HashUtils.md5(password)}'")
                Log.d("LoginDebug", "User found: ${user != null}")

                if (user != null) {
                    // saving login state and user info to SharedPreferences
                    // ref: https://developer.android.com/training/data-storage/shared-preferences
                    getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isRegistered", true)
                        .putInt("USER_ID", user.id.toInt())
                        .putString("loggedInUsername", user.username)
                        .putString("loggedInFullName", user.fullName)
                        .putString("loggedInEmail", user.email)
                        .apply()

                    // switching back to main thread to navigate
                    // ref: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    // showing error on both fields if credentials dont match any user in the DB
                    withContext(Dispatchers.Main) {
                        tilUsername.error = "Incorrect username or password"
                        tilPassword.error = "Incorrect username or password"
                    }
                }
            }
        }
    }
}

/*
References:

Android Developers, 2024. Introduction to Activities.
Available at: https://developer.android.com/guide/components/activities/intro-activities
[Accessed 27 April 2026].

Android Developers, 2024. The Activity Lifecycle.
Available at: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
[Accessed 27 April 2026].

Android Developers, 2024. Save data in a local database using Room.
Available at: https://developer.android.com/training/data-storage/room
[Accessed 27 April 2026].

Android Developers, 2024. View - findViewById.
Available at: https://developer.android.com/reference/android/view/View#findViewById(int)
[Accessed 27 April 2026].

Android Developers, 2024. Intents and Intent Filters.
Available at: https://developer.android.com/guide/components/intents-filters
[Accessed 27 April 2026].

Android Developers, 2024. Patterns - EMAIL_ADDRESS.
Available at: https://developer.android.com/reference/android/util/Patterns#EMAIL_ADDRESS
[Accessed 27 April 2026].

Android Developers, 2024. Use Kotlin coroutines with lifecycle-aware components.
Available at: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
[Accessed 27 April 2026].

Android Developers, 2024. Improve app performance with Kotlin coroutines.
Available at: https://developer.android.com/kotlin/coroutines/coroutines-adv#main-safety
[Accessed 27 April 2026].

Android Developers, 2024. Access data using Room DAOs.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
[Accessed 27 April 2026].

Android Developers, 2024. Save key-value data with SharedPreferences.
Available at: https://developer.android.com/training/data-storage/shared-preferences
[Accessed 27 April 2026].

Android Developers, 2024. Log.
Available at: https://developer.android.com/reference/android/util/Log
[Accessed 27 April 2026].
*/