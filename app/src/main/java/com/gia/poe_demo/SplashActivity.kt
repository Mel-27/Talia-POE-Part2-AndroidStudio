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

    // Total splash duration in milliseconds
    private val SPLASH_DURATION = 3000L
    // Dot animation interval
    private val DOT_INTERVAL = 400L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // 👈 match your XML file name

        val tvLoadingDots = findViewById<android.widget.TextView>(R.id.tvLoadingDots)

        loadingDotsHandler = Handler(Looper.getMainLooper())

        // Animate the dots: "." → ".." → "..." → repeat
        loadingDotsRunnable = object : Runnable {
            override fun run() {
                tvLoadingDots.text = dotStates[dotIndex % dotStates.size]
                dotIndex++
                loadingDotsHandler.postDelayed(this, DOT_INTERVAL)
            }
        }
        loadingDotsHandler.post(loadingDotsRunnable)

        // Navigate to MainActivity after the splash duration
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Remove splash from back stack
        }, SPLASH_DURATION)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up to prevent memory leaks
        loadingDotsHandler.removeCallbacks(loadingDotsRunnable)
    }
}