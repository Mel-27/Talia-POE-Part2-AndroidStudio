package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.gia.poe_demo.databinding.ActivityAccountBinding

/**
 * AccountActivity — profile and settings screen.
 *
 * Features implemented:
 *  - Light / Dark mode toggle using the two-pill selector in the layout
 *    (cardLightMode + tvDarkMode acting as a segmented toggle)
 *  - Preference persisted through SessionManager (SharedPreferences)
 *  - AppCompatDelegate.setDefaultNightMode applied globally then recreate()
 *    so every activity in the stack picks up the change immediately
 *  - Sign Out row navigates back to ExpensesListActivity (stub until Login is done)
 *
 * Reference: Material3 DayNight theme; IIE PROG7313 Module Guide (2026)
 * Layout: res/layout/activity_account.xml
 */

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = SessionManager(this)
        applyTheme()
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupThemeToggle()
        setupSignOut()
        setupBottomNav()
    }

    // Apply saved theme

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (session.isDarkMode()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // Sync the toggle visuals to the saved preference

    /**
     * The layout has two visually distinct options:
     *   cardLightMode — white pill (active look when light is selected)
     *   tvDarkMode    — plain text (active look when dark is selected)
     * We update their appearance to match the saved preference.
     */

    // Theme toggle

    /**
     * Sets up click listeners on both the Light card and Dark text.
     * On each tap: save preference → apply new mode globally → recreate() this activity.
     * Reference: AppCompatDelegate; IIE PROG7313 (2026)
     */

    private fun setupThemeToggle() {
        // Tapping "☀ Light" selects light mode
        binding.cardLightMode?.setOnClickListener {
            if (session.isDarkMode()) {
                session.setDarkMode(false)
                android.util.Log.d("AccountActivity", "Switched to LIGHT mode")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                recreate()
            }
        }

        // Tapping "🌙 Dark" selects dark mode
        binding.tvDarkMode?.setOnClickListener {
            if (!session.isDarkMode()) {
                session.setDarkMode(true)
                android.util.Log.d("AccountActivity", "Switched to DARK mode")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                recreate()
            }
        }
    }

    // Sign out
    private fun setupSignOut() {
        binding.rowSignOut?.setOnClickListener {
            android.util.Log.d("AccountActivity", "User signed out")
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ExpensesListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    // Bottom nav

    private fun setupBottomNav() {
        // Account screen has no bottom nav by design

    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
}