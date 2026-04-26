package com.gia.poe_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = intent.getIntExtra("USER_ID", -1)

        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }


}