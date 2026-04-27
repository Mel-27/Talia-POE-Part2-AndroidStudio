package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)

        findViewById<android.view.View>(R.id.tabSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnLogin).setOnClickListener {
            val input = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (input.isEmpty()) {
                tilUsername.error = "Required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                tilPassword.error = "Required"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userDao.loginUser(input, password)

                if (user != null) {

                    val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                    prefs.edit().putBoolean("isRegistered", true).apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    tilPassword.error = "Invalid username or password"
                }
            }
        }

        findViewById<android.view.View>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}