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

/*
References
-Google (n.d.) Room Persistence Library.
Available at: https://developer.android.com/training/data-storage/room
(Accessed: 27 April 2026).

-Google (n.d.) Android Developers: Activities.
Available at: https://developer.android.com/guide/components/activities/intro-activities
(Accessed: 27 April 2026).

-Google (n.d.) Kotlin Coroutines on Android.
Available at: https://developer.android.com/kotlin/coroutines
(Accessed: 27 April 2026).

-Google (n.d.) View Binding.
Available at: https://developer.android.com/topic/libraries/view-binding
(Accessed: 27 April 2026).

-JetBrains (n.d.) Kotlin Language Documentation.
Available at: https://kotlinlang.org/docs/home.html
(Accessed: 27 April 2026).

-Google (n.d.) Data Access Objects (DAO) in Room.
Available at: https://developer.android.com/training/data-storage/room/accessing-data
(Accessed: 27 April 2026).

-Google (n.d.) Android Lifecycle and LifecycleScope.
Available at: https://developer.android.com/topic/libraries/architecture/lifecycle
(Accessed: 27 April 2026).
 */
