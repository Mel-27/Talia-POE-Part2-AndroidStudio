package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BadgesActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        db = AppDatabase.getInstance(this)

        loadBadgeData()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadBadgeData()
    }

    private fun loadBadgeData() {

        val prefs = getSharedPreferences("APP", MODE_PRIVATE)
        val userId = prefs.getLong("USER_ID", -1L)

        lifecycleScope.launch {

            if (userId == -1L) return@launch

            val points = withContext(Dispatchers.IO) {
                db.honeyPointsDao()
                    .getPointsForUser(userId.toInt())
                    ?.points
            } ?: 0

            findViewById<TextView>(R.id.tvHoneyPoints)?.text = "$points 🍯"

            val (current, target) =
                GamificationManager.getProgressToNextBadge(points)

            findViewById<TextView>(R.id.tvNextBadgeLabel)?.text =
                GamificationManager.getNextBadgeLabel(points)

            findViewById<ProgressBar>(R.id.progressNextBadge)?.apply {
                max = target.coerceAtLeast(1)
                progress = current.coerceAtMost(target)
            }

            val earnedNames = GamificationManager
                .getEarnedBadges(points)
                .map { it.name }

            updateBadgeCard(R.id.cardBadge1, earnedNames.contains("Worker Bee"))
            updateBadgeCard(R.id.cardBadge2, earnedNames.contains("Honey Collector"))
            updateBadgeCard(R.id.cardBadge3, earnedNames.contains("Honey Hoarder"))
            updateBadgeCard(R.id.cardBadge4, earnedNames.contains("Queen Bee"))
        }
        }

    private fun updateBadgeCard(cardId: Int, earned: Boolean) {

        val card = findViewById<CardView>(cardId) ?: return

        // SAFE: always assume first TextView inside card layout is status label
        val statusLabel = findFirstTextView(card)

        statusLabel?.text = if (earned) "✓ Earned" else "🔒 Locked"
        statusLabel?.setTextColor(
            getColor(if (earned) R.color.success_green else R.color.muted_text)
        )

        card.alpha = if (earned) 1f else 0.45f
    }

    private fun findFirstTextView(view: View): TextView? {
        if (view is TextView) return view
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val result = findFirstTextView(view.getChildAt(i))
                if (result != null) return result
            }
        }
        return null
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navExpenses)?.setOnClickListener {
            startActivity(Intent(this, ExpensesListActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.fabAddExpense)?.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        findViewById<View>(R.id.navGoals)?.setOnClickListener {
            startActivity(Intent(this, BudgetGoalsActivity::class.java))
            finish()
        }
    }
}