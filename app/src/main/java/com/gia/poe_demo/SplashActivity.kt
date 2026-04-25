package com.gia.poe_demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var loadingDotsHandler: Handler
    private lateinit var loadingDotsRunnable: Runnable
    private val dotStates = listOf(".", "..", "...")
    private var dotIndex = 0

    private val SPLASH_DURATION = 3000L
    private val DOT_INTERVAL = 400L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val tvLoadingDots = findViewById<android.widget.TextView>(R.id.tvLoadingDots)

        loadingDotsHandler = Handler(Looper.getMainLooper())

        loadingDotsRunnable = object : Runnable {
            override fun run() {
                tvLoadingDots.text = dotStates[dotIndex % dotStates.size]
                dotIndex++
                loadingDotsHandler.postDelayed(this, DOT_INTERVAL)
            }
        }
        loadingDotsHandler.post(loadingDotsRunnable)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
            val isRegistered = prefs.getBoolean("isRegistered", false)

            if (isRegistered) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, SPLASH_DURATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDotsHandler.removeCallbacks(loadingDotsRunnable)
    }
}