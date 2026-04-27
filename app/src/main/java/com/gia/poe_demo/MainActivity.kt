package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// This is the entry point of the app that immediately redirects
// the user to the splash screen when the app launches.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, activity_splash::class.java))
        finish()
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
