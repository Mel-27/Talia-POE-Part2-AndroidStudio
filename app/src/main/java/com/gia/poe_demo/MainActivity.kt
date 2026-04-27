package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

// Main screen of the application - acts as a navigation hub
// ref: https://developer.android.com/guide/components/activities/intro-activities
class MainActivity : AppCompatActivity() {

    // loads the main screen when the activity starts
    // ref: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
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
        // ref: https://developer.android.com/reference/android/view/View#findViewById(int)
        findViewById<TextView>(R.id.tvAvatarInitial).text = loggedInUsername.first().uppercase()

        // retrieve the logged in user's ID passed from previous activity
        // ref: https://developer.android.com/reference/android/content/Intent#getIntExtra(java.lang.String,%20int)
        val userId = intent.getIntExtra("USER_ID", -1)

        // floating action button / card to add a new expense
        // Adapted from (Medium, 2018)
        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            // passing the user ID to AddExpenseActivity using an explicit Intent
            // ref: https://developer.android.com/guide/components/intents-filters
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        // avatar/profile button - opens AccountActivity
        // Adapted from (Medium, 2018)
        findViewById<CardView>(R.id.btnAvatar).setOnClickListener {
            // navigating to AccountActivity using an explicit Intent
            // ref: https://developer.android.com/guide/components/intents-filters
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}

/*
References:

Android Developers, 2024. Introduction to Activities.
Available at: https://developer.android.com/guide/components/activities/intro-activities
[Accessed 21 April 2026].

Android Developers, 2024. The Activity Lifecycle.
Available at: https://developer.android.com/guide/components/activities/activity-lifecycle#onCreate
[Accessed 21 April 2026].

Android Developers, 2024. Save key-value data with SharedPreferences.
Available at: https://developer.android.com/training/data-storage/shared-preferences
[Accessed 22 April 2026].

Android Developers, 2024. View - findViewById.
Available at: https://developer.android.com/reference/android/view/View#findViewById(int)
[Accessed 21 April 2026].

Android Developers, 2024. Intent - getIntExtra.
Available at: https://developer.android.com/reference/android/content/Intent#getIntExtra(java.lang.String,%20int)
[Accessed 20 April 2026].

Android Developers, 2024. Intents and Intent Filters.
Available at: https://developer.android.com/guide/components/intents-filters
[Accessed 20 April 2026].

Android Ideas (Medium), 2018. findViewById in Kotlin.
Available at: https://medium.com/android-ideas/findviewbyid-in-kotlin-ce4d22193base
[Accessed 21 April 2026].
*/