package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

// Main screen of the application - acts as a navigation hub
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // retrieve the logged in username from SharedPreferences
        // ref: https://developer.android.com/training/data-storage/shared-preferences
        val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
        val loggedInUsername = prefs.getString("loggedInUsername", "User") ?: "User"

        // display the logged in users name in the greeting
        // ref: https://developer.android.com/reference/android/view/View#findViewById(int)
        findViewById<TextView>(R.id.tvUserName).text = "$loggedInUsername 🐝"

        // set the avatar initial to the first letter of the username
        findViewById<TextView>(R.id.tvAvatarInitial).text = loggedInUsername.first().uppercase()

        // retrieve the logged in user's ID passed from previous activity
        val userId = intent.getIntExtra("USER_ID", -1)

        // floating action button / card to add a new expense
        // Adapted from (Medium, 2018)
        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // avatar/profile button - opens AccountActivity
        // Adapted from (Medium, 2018)
        findViewById<CardView>(R.id.btnAvatar).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}

/*
References:
Android Ideas (Medium), 2018. findViewById in Kotlin.
Available at: https://medium.com/android-ideas/findviewbyid-in-kotlin-ce4d22193c79
[Accessed 27 April 2026].
*/