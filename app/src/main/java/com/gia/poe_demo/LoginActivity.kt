package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
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

        // grabbing the Room DB instance using the singleton pattern
        // ref: https://developer.android.com/training/data-storage/room
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // used findViewById() to grab the input fields and text input layouts
        // ref: https://developer.android.com/reference/android/view/View#findViewById(int)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)

        // tab click navigates to RegisterActivity using an explicit Intent
        // ref: https://developer.android.com/guide/components/intents-filters
        findViewById<android.view.View>(R.id.tabSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnLogin).setOnClickListener {
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

            // used lifecycleScope.launch to run the Room DB query off the main thread
            // ref: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
            lifecycleScope.launch(Dispatchers.IO) {

                // hashing the password before comparing with the stored hash
                val user = userDao.loginUser(input, HashUtils.md5(password))

                if (user != null) {

                    // saving login state to SharedPreferences so the app remembers the user
                    // ref: https://developer.android.com/training/data-storage/shared-preferences
                    val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                    prefs.edit()
                        .putBoolean("isRegistered", true)
                        .putString("loggedInUsername", user.username)
                        .apply()

                    // switching back to main thread to navigate
                    // ref: https://developer.android.com/reference/android/app/Activity#finish()
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

        // tvRegister click also navigates to RegisterActivity
        // ref: https://developer.android.com/guide/components/intents-filters
        findViewById<android.view.View>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}