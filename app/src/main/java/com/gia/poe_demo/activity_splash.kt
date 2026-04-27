package com.gia.poe_demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        setContentView(R.layout.onboarding_screen_1)
        setContentView(R.layout.onboarding_screen_2)
        setContentView(R.layout.onboarding_screen_3)
        setContentView(R.layout.onboarding_screen_4)
        setContentView(R.layout.onboarding_screen_5)
        }
    }
