package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class BadgesActivity : AppCompatActivity() {

    private val TAG = "BadgesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        Log.d(TAG, "BadgesActivity started")

        setupBottomNav()
        loadAndDisplayPoints()
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: refreshing points")
        loadAndDisplayPoints()
    }


    private fun loadAndDisplayPoints() {
        lifecycleScope.launch {
            try {
                val prefs  = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                val userId = prefs.getInt("loggedInUserId", -1)
                Log.d(TAG, "Loading points for userId=$userId")

                val db = AppDatabase.getInstance(this@BadgesActivity)


                var honeyData = db.honeyPointsDao().getPointsForUser(userId)
                if (honeyData == null) {
                    Log.d(TAG, "No points record found — creating new one")
                    honeyData = HoneyPoints(userId = userId, points = 0)
                    db.honeyPointsDao().upsert(honeyData)
                }

                val points = honeyData.points
                Log.d(TAG, "User has $points Honey Points")


                updatePointsDisplay(points)
                updateProgressBar(points)
                updateBadgeGallery(points)

            } catch (e: Exception) {
                Log.e(TAG, "Error loading points: ${e.message}", e)
            }
        }
    }

    private fun updatePointsDisplay(points: Int) {
        findViewById<TextView>(R.id.tvHoneyPoints).text = "$points 🍯"

        val nextLabel = GamificationManager.getNextBadgeLabel(points)

        Log.d(TAG, "Next badge label: $nextLabel")
    }

    private fun updateProgressBar(points: Int) {
        val (current, target) = GamificationManager.getProgressToNextBadge(points)
        val progressBar = findViewById<ProgressBar>(R.id.progressNextBadge)
        progressBar.max      = target
        progressBar.progress = current

        Log.d(TAG, "Progress: $current / $target")
    }


    private fun updateBadgeGallery(points: Int) {
        val earnedBadges = GamificationManager.getEarnedBadges(points)
        val earnedNames  = earnedBadges.map { it.name }.toSet()


        updateSingleBadge(
            cardId     = R.id.cardBadge1,
            badgeName  = "Worker Bee",
            earnedNames = earnedNames
        )
        updateSingleBadge(
            cardId     = R.id.cardBadge2,
            badgeName  = "Honey Collector",
            earnedNames = earnedNames
        )
        updateSingleBadge(
            cardId     = R.id.cardBadge3,
            badgeName  = "Honey Hoarder",
            earnedNames = earnedNames
        )
        updateSingleBadge(
            cardId     = R.id.cardBadge4,
            badgeName  = "Queen Bee",
            earnedNames = earnedNames
        )

        Log.d(TAG, "Earned badges: $earnedNames")
    }


    private fun updateSingleBadge(
        cardId: Int,
        badgeName: String,
        earnedNames: Set<String>
    ) {
        val card = findViewById<CardView>(cardId) ?: return
        val isEarned = badgeName in earnedNames

        if (isEarned) {
            card.alpha = 1.0f
            // Find the status TextView inside this card (first TextView child)
            val statusTv = card.findViewWithTag<TextView>("tvBadgeStatus_$badgeName")
                ?: card.getChildAt(0)?.let {
                    (it as? android.widget.LinearLayout)?.getChildAt(0) as? TextView
                }
            statusTv?.text      = "✓ Earned"
            statusTv?.setTextColor(getColor(R.color.success_green))
        } else {
            card.alpha = 0.45f
            val statusTv = card.findViewWithTag<TextView>("tvBadgeStatus_$badgeName")
                ?: card.getChildAt(0)?.let {
                    (it as? android.widget.LinearLayout)?.getChildAt(0) as? TextView
                }
            statusTv?.text      = "🔒 Locked"
            statusTv?.setTextColor(getColor(R.color.muted_text))
        }
    }



    private fun setupBottomNav() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            Log.d(TAG, "navHome clicked")
            finish()
        }

        findViewById<LinearLayout>(R.id.navExpenses).setOnClickListener {
            Log.d(TAG, "navExpenses clicked")
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }

        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            Log.d(TAG, "FAB clicked")
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.navGoals).setOnClickListener {

        }


        findViewById<LinearLayout>(R.id.navBadges).setOnClickListener {
            Log.d(TAG, "Already on Badges")
        }
    }
}