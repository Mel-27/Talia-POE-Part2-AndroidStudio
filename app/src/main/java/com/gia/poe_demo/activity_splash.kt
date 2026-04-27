package com.gia.poe_demo

/**
import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * activity_splash — launch screen shown briefly before the main app loads.
 * Reference: IIE PROG7313 Module Guide (2026)
 */

class activity_splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme before layout inflates
        val session = SessionManager(this)
        if (session.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_splash)

        // Navigate to main screen after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ExpensesListActivity::class.java))
            finish()
        }, 2000)
    }
}
        */