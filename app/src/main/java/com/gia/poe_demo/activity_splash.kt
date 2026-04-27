package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val screens = listOf(
        R.layout.activity_splash,
        R.layout.onboarding_screen_1,
        R.layout.onboarding_screen_2,
        R.layout.onboarding_screen_3,
        R.layout.onboarding_screen_4,
        R.layout.onboarding_screen_5
    )

    private var currentScreen = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showScreen(currentScreen)
    }

    private fun showScreen(index: Int) {
        if (index !in screens.indices) {
            goToHome()
            return
        }

        setContentView(screens[index])

        val btnNext = findViewById<Button?>(R.id.btnNext)
        val btnSkip = findViewById<Button?>(R.id.btnSkip)
        val btnGetStarted = findViewById<Button?>(R.id.btnGetStarted)

        btnNext?.setOnClickListener {
            if (currentScreen < screens.lastIndex) {
                currentScreen++
                showScreen(currentScreen)
            } else {
                goToHome()
            }
        }

        btnSkip?.setOnClickListener {
            goToHome()
        }

        btnGetStarted?.setOnClickListener {
            goToHome()
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}