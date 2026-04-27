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
        //Adapted from (GeekforGeeks, 2022-  How to check if an app is in dark mode and change it to light mode in Android)
        findViewById<CardView>(R.id.btnLight).setOnClickListener {

            // Display feedback to user
            Toast.makeText(this, "Light tapped", Toast.LENGTH_SHORT).show()

            // Set app theme to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // Dark mode button - switches app theme to dark mode
        //Adapted from (GeekforGeeks, 2022)
        findViewById<TextView>(R.id.btnDark).setOnClickListener {

            // Display feedback to user
            Toast.makeText(this, "Dark tapped", Toast.LENGTH_SHORT).show()

            // Set app theme to dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
/*
References:

GeeksforGeeks, 2022. How to check if an app is in dark mode and change it to light mode in Android.
Available at: https://www.geeksforgeeks.org/android/how-to-check-if-an-app-is-in-dark-mode-and-change-it-to-light-mode-in-android/
[Accessed 27 April 2026].
 */