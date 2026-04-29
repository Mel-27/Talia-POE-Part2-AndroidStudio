package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView


//Activity responsible for handling account settings
class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Set the layout for the account screen
        setContentView(R.layout.activity_account)

        //Back button - closes the activity and returns to previous screen
        findViewById<TextView>(R.id.tvBack).setOnClickListener { finish() }

        // Light mode button - switches app theme to light mode
        //Adapted from (GeekforGeeks, 2022)
        findViewById<CardView>(R.id.btnLight).setOnClickListener {

            // Display feedback to user
            Toast.makeText(this, "Light tapped", Toast.LENGTH_SHORT).show()

            // Set app theme to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // Dark mode button - switches app theme to dark mode
        //Adapted from (GeekforGeeks, 2022)
        findViewById<android.view.View>(R.id.rowSignOut)?.setOnClickListener {
            // Clear all session data
            getSharedPreferences("APP", MODE_PRIVATE).edit().clear().apply()
            getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE).edit().clear().apply()
            getSharedPreferences("budget_bee_prefs", MODE_PRIVATE).edit().clear().apply()

            // Go back to login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


    }
}
/*
References:
GeeksforGeeks, 2022. How to check if an app is in dark mode and change it to light mode in Android.
Available at: https://www.geeksforgeeks.org/android/how-to-check-if-an-app-is-in-dark-mode-and-change-it-to-light-mode-in-android/
[Accessed 27 April 2026].
 */