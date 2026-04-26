package com.gia.poe_demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.databinding.ActivityBudgetGoalsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetGoalsBinding
    private lateinit var db: AppDatabase
    private val currencyFormat = NumberFormat.getNumberInstance(Locale("en", "ZA"))

    // Current month key, e.g. "2026-03"
    private val currentMonthYear: String
        get() {
            val cal = Calendar.getInstance()
            return "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"
        }

    // Placeholder spending per category — in Part 2 these will come from ExpenseDao
    private val spending = mapOf(
        "groceries"     to 640.0,
        "entertainment" to 510.0,
        "transport"     to 1050.0,
        "food"          to 340.0,
        "health"        to 250.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        loadGoalsForCurrentMonth()
        setupUpdateButtons()
        setupNavigation()
    }

    // ── Load saved goals from Room and populate the UI ──────────────────────
    private fun loadGoalsForCurrentMonth() {
        lifecycleScope.launch {
            val goal = withContext(Dispatchers.IO) {
                db.budgetGoalDao().getGoalForMonth(currentMonthYear)
            } ?: buildDefaultGoal()

            renderGoals(goal)
        }
    }

    private fun buildDefaultGoal() = BudgetGoal(
        monthYear            = currentMonthYear,
        totalMonthlyBudget   = 5000.0,
        minMonthlyGoal       = 0.0,
        maxMonthlyGoal       = 5000.0,
        groceriesLimit       = 1000.0,
        entertainmentLimit   = 500.0,
        transportLimit       = 1500.0,
        foodLimit            = 1000.0,
        healthLimit          = 500.0
    )

    //  Render all UI from a BudgetGoal object
    private fun renderGoals(goal: BudgetGoal) {
        val totalSpent = spending.values.sum()
        val remaining  = goal.totalMonthlyBudget - totalSpent
        val usedPct    = ((totalSpent / goal.totalMonthlyBudget) * 100).toInt().coerceIn(0, 100)

        // ── Header summary card ──
        binding.tvTotalBudget.text   = "R ${fmt(goal.totalMonthlyBudget)}"
        binding.tvBudgetSubtitle.text =
            "R ${fmt(totalSpent)} spent · R ${fmt(remaining)} remaining"
        binding.progressTotalBudget.progress = usedPct
        binding.tvUsedPct.text  = "$usedPct% used"
        binding.tvFreePct.text  = "${100 - usedPct}% free"
        binding.tvBudgetStatus.text = if (usedPct < 80) "On Track ✓" else "⚠ Watch spend"

        // Monthly budget edit field
        binding.etMonthlyBudget.setText(goal.totalMonthlyBudget.toInt().toString())

        // ── Min / Max goal fields ──
        binding.etMinGoal.setText(goal.minMonthlyGoal.toInt().toString())
        binding.etMaxGoal.setText(goal.maxMonthlyGoal.toInt().toString())

        // ── Bar chart heights (max bar = 130 dp, scaled to highest spend) ──
        val maxSpend = spending.values.maxOrNull() ?: 1.0
        fun barDp(category: String): Int =
            ((spending[category]!! / maxSpend) * 130).toInt().coerceAtLeast(8)

        setBarHeight(binding.barGroceries,     barDp("groceries"))
        setBarHeight(binding.barEntertainment, barDp("entertainment"))
        setBarHeight(binding.barTransport,     barDp("transport"))
        setBarHeight(binding.barFood,          barDp("food"))
        setBarHeight(binding.barHealth,        barDp("health"))

        // ── Category cards ──
        renderCategory(
            spent      = spending["groceries"]!!,
            limit      = goal.groceriesLimit,
            progressView = binding.progressGroceries,
            tvSpent    = binding.tvGroceriesSpent,
            tvLimit    = binding.tvGroceriesLimit,
            etLimit    = binding.etGroceriesLimit
        )
        renderCategory(
            spent      = spending["entertainment"]!!,
            limit      = goal.entertainmentLimit,
            progressView = binding.progressEntertainment,
            tvSpent    = binding.tvEntertainmentSpent,
            tvLimit    = binding.tvEntertainmentLimit,
            etLimit    = binding.etEntertainmentLimit,
            overLimitBadge = binding.cardOverLimitEntertainment
        )
        renderCategory(
            spent      = spending["transport"]!!,
            limit      = goal.transportLimit,
            progressView = binding.progressTransport,
            tvSpent    = binding.tvTransportSpent,
            tvLimit    = binding.tvTransportLimit,
            etLimit    = binding.etTransportLimit
        )
        renderCategory(
            spent      = spending["food"]!!,
            limit      = goal.foodLimit,
            progressView = binding.progressFood,
            tvSpent    = binding.tvFoodSpent,
            tvLimit    = binding.tvFoodLimit,
            etLimit    = binding.etFoodLimit
        )
        renderCategory(
            spent      = spending["health"]!!,
            limit      = goal.healthLimit,
            progressView = binding.progressHealth,
            tvSpent    = binding.tvHealthSpent,
            tvLimit    = binding.tvHealthLimit,
            etLimit    = binding.etHealthLimit
        )
    }

    private fun renderCategory(
        spent: Double,
        limit: Double,
        progressView: android.widget.ProgressBar,
        tvSpent: android.widget.TextView,
        tvLimit: android.widget.TextView,
        etLimit: android.widget.EditText,
        overLimitBadge: androidx.cardview.widget.CardView? = null
    ) {
        val pct = ((spent / limit) * 100).toInt().coerceIn(0, 100)
        progressView.progress = pct
        tvSpent.text = "R ${fmt(spent)} spent"
        tvLimit.text = "R ${fmt(limit)} limit"
        etLimit.setText(limit.toInt().toString())
        overLimitBadge?.visibility =
            if (spent > limit) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun setBarHeight(view: android.view.View, dp: Int) {
        val px = (dp * resources.displayMetrics.density).toInt()
        val params = view.layoutParams
        params.height = px
        view.layoutParams = params
    }

    // ── Wire up every Update button to save changes to Room ────────────────
    private fun setupUpdateButtons() {

        // Monthly budget + min/max goals
        binding.btnUpdateMonthlyBudget.setOnClickListener {
            val total = binding.etMonthlyBudget.text.toString().toDoubleOrNull()
            val min   = binding.etMinGoal.text.toString().toDoubleOrNull()
            val max   = binding.etMaxGoal.text.toString().toDoubleOrNull()

            if (total == null || total <= 0) {
                toast("Please enter a valid monthly budget"); return@setOnClickListener
            }
            if (min != null && max != null && min > max) {
                toast("Minimum goal cannot exceed maximum goal"); return@setOnClickListener
            }

            saveGoal { current ->
                current.copy(
                    totalMonthlyBudget = total,
                    minMonthlyGoal     = min ?: current.minMonthlyGoal,
                    maxMonthlyGoal     = max ?: current.maxMonthlyGoal
                )
            }
        }

        // Per-category limit buttons
        binding.btnUpdateGroceries.setOnClickListener {
            updateLimit("groceries", binding.etGroceriesLimit.text.toString().toDoubleOrNull()) { goal, v ->
                goal.copy(groceriesLimit = v)
            }
        }
        binding.btnUpdateEntertainment.setOnClickListener {
            updateLimit("entertainment", binding.etEntertainmentLimit.text.toString().toDoubleOrNull()) { goal, v ->
                goal.copy(entertainmentLimit = v)
            }
        }
        binding.btnUpdateTransport.setOnClickListener {
            updateLimit("transport", binding.etTransportLimit.text.toString().toDoubleOrNull()) { goal, v ->
                goal.copy(transportLimit = v)
            }
        }
        binding.btnUpdateFood.setOnClickListener {
            updateLimit("food", binding.etFoodLimit.text.toString().toDoubleOrNull()) { goal, v ->
                goal.copy(foodLimit = v)
            }
        }
        binding.btnUpdateHealth.setOnClickListener {
            updateLimit("health", binding.etHealthLimit.text.toString().toDoubleOrNull()) { goal, v ->
                goal.copy(healthLimit = v)
            }
        }
    }

    private fun updateLimit(
        category: String,
        newLimit: Double?,
        copyFn: (BudgetGoal, Double) -> BudgetGoal
    ) {
        if (newLimit == null || newLimit <= 0) {
            toast("Enter a valid $category limit"); return
        }
        saveGoal { current -> copyFn(current, newLimit) }
    }

    /** Reads current goal (or default), applies [transform], saves, then re-renders. */
    private fun saveGoal(transform: (BudgetGoal) -> BudgetGoal) {
        lifecycleScope.launch {
            val current = withContext(Dispatchers.IO) {
                db.budgetGoalDao().getGoalForMonth(currentMonthYear)
            } ?: buildDefaultGoal()

            val updated = transform(current)

            withContext(Dispatchers.IO) {
                db.budgetGoalDao().insertOrUpdate(updated)
            }

            toast("Saved ✓")
            renderGoals(updated)
        }
    }

    // ── Bottom nav ──────────────────────────────────────────────────────────
    private fun setupNavigation() {
        binding.navHome.setOnClickListener { finish() }
        binding.navExpenses.setOnClickListener {
            // startActivity(Intent(this, ExpensesActivity::class.java))
        }
        binding.fabAddExpense.setOnClickListener {
            // startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        binding.navGoals.setOnClickListener { /* already here */ }
        binding.navBadges.setOnClickListener {
            // startActivity(Intent(this, BadgesActivity::class.java))
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private fun fmt(value: Double): String = currencyFormat.format(value)
    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
