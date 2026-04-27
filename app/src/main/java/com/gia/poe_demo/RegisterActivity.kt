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

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val tilConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)

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

            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                if (fullName.isEmpty()) findViewById<TextInputLayout>(R.id.tilFullName).error = "Required"
                if (email.isEmpty()) tilEmail.error = "Required"
                if (username.isEmpty()) tilUsername.error = "Required"
                if (password.isEmpty()) tilPassword.error = "Required"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                tilConfirmPassword.error = "Passwords do not match"
                return@setOnClickListener
            }

            lifecycleScope.launch {

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

                userDao.registerUser(User(
                    fullName = fullName,
                    email = email,
                    username = username,
                    password = password
                ))

                val allUsers = userDao.getAllUsers()
                android.util.Log.d("RoomDB", "Users in database: $allUsers")

                val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                prefs.edit().putBoolean("isRegistered", true).apply()

                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }
        }


        findViewById<android.view.View>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}