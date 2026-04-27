package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

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

            // basic field validation before querying the database
            if (input.isEmpty()) {
                tilUsername.error = "Required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tilPassword.error = "Required"
                return@setOnClickListener
            }

            // used lifecycleScope.launch to run the Room DB query off the main thread
            // ref: https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope
            lifecycleScope.launch {
                val user = userDao.loginUser(input, password)

                if (user != null) {

                    // saving login state to SharedPreferences so the app remembers the user
                    // ref: https://developer.android.com/training/data-storage/shared-preferences
                    val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                    prefs.edit().putBoolean("isRegistered", true).apply()

                    // navigating to MainActivity and calling finish() so the user cant go back to login
                    // ref: https://developer.android.com/reference/android/app/Activity#finish()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    tilPassword.error = "Invalid username or password"
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