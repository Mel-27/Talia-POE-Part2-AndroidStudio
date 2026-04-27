package com.gia.poe_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.LinearLayout
import androidx.cardview.widget.CardView

//Main screen of the application- acts as a navigation hub
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //Set the layout for the main screen
        setContentView(R.layout.activity_main)

        //Retrieve the logged-in user's ID passed from previous activity
        val userId = intent.getIntExtra("USER_ID", -1)

        // Floating action button / card to add a new expense
        //Adapted from (Medium ,2018)
        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {

            // Create intent to navigate to AddExpenseActivity
            val intent = Intent(this, AddExpenseActivity::class.java)

            // Pass user ID to the next activity
            intent.putExtra("USER_ID", userId)

            // Start AddExpenseActivity
            startActivity(intent)

        }
        // Avatar/profile button - opens AccountActivity
        //Adapted from (Medium ,2018)
        findViewById<CardView>(R.id.btnAvatar).setOnClickListener {

            // Navigate to account/settings screen
            startActivity(Intent(this, AccountActivity::class.java))
        }
          //Navigate to badges screen
        findViewById<LinearLayout>(R.id.navBadges).setOnClickListener {
            startActivity(Intent(this, BadgesActivity::class.java))
        }

    }


}

/*
References:

Android Ideas (Medium), 2018. findViewById in Kotlin.
Available at: https://medium.com/android-ideas/findviewbyid-in-kotlin-ce4d22193c79
[Accessed 27 April 2026].
 */