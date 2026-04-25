package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private var currentScreen = 1
    private val totalScreens = 5

    private val layouts = mapOf(
        1 to R.layout.onboarding_screen_1,
        2 to R.layout.onboarding_screen_2,
        3 to R.layout.onboarding_screen_3,
        4 to R.layout.onboarding_screen_4,
        5 to R.layout.onboarding_screen_5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadScreen(currentScreen)
    }

    private fun loadScreen(screen: Int) {
        setContentView(layouts[screen]!!)

        val nextBtn = findViewById<android.view.View>(R.id.btnNext)
        nextBtn?.setOnClickListener {
            if (currentScreen < totalScreens) {
                currentScreen++
                loadScreen(currentScreen)
            } else {
                goToLogin()
            }
        }

        val skipBtn = findViewById<android.view.View>(R.id.btnSkip)
        skipBtn?.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}