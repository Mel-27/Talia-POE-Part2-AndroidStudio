package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import com.gia.poe_demo.data.entities.User
import com.gia.poe_demo.HashUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<android.view.View>(R.id.btnLogin)

        val tabSignUp = findViewById<android.view.View>(R.id.tabSignUp)
        val tvRegister = findViewById<android.view.View>(R.id.tvRegister)

        tabSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        btnLogin.setOnClickListener {
            val input = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (input.isEmpty() || password.isEmpty()) {
                etPassword.error = "Please fill all fields"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var user: User? = null

                // 1. Try fullName with hash
                user = userDao.loginUserByHash(input, HashUtils.md5(password))

                // 2. Try fullName with plain
                if (user == null) user = userDao.loginUserByPlain(input, password)

                // 3. Try email with hash
                if (user == null) user = userDao.loginUserByEmailHash(input, HashUtils.md5(password))

                // 4. Try email with plain
                if (user == null) user = userDao.loginUserByEmailPlain(input, password)

                Log.d("LoginDebug", "Input: '$input'")
                Log.d("LoginDebug", "Password: '$password'")
                Log.d("LoginDebug", "Hash: '${HashUtils.md5(password)}'")
                Log.d("LoginDebug", "User found: ${user != null}")

                if (user != null) {
                    // Login success!
                    getSharedPreferences("APP", MODE_PRIVATE)
                        .edit().putLong("USER_ID", user.id).apply()

                    getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("loggedInUsername", user.fullName)
                        .apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    etPassword.error = "Invalid details"
                }
            }
        }
    }
}