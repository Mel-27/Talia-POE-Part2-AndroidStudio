package com.gia.poe_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

/**
 * ExpenseListActivity - Shows the user's full expense history.
 *
 * References:
 * Used for onCreate() and onResume() lifecycle methods to load and refresh expenses correctly
 * when the screen starts and returns to focus:
 * Android Developers. (2019). Understand the Activity Lifecycle  |  Android Developers. Available at:
 * https://developer.android.com/guide/components/activities/activity-lifecycle.
 * [Accessed 27 Apr. 2026]
 *
 * Used for Intent navigation when opening ReceiptViewActivity and passing data (photoPath)
 * between screens:
 * Android Developers. (2019). Intents and Intent Filters  |  Android Developers. Available at:
 * https://developer.android.com/guide/components/intents-filters.
 * [Accessed 27 Apr. 2026]
 *
 * Used for implementing RecyclerView setup, adapter binding, and displaying a scrollable list
 * of expenses:
 * GeeksforGeeks (2025). RecyclerView in Android with Example. GeeksforGeeks. Available at:
 * https://www.geeksforgeeks.org/android/android-recyclerview/.
 * [Accessed 26 Apr. 2026]
 *
 * Used for loading data from Room database and updating RecyclerView when the expense list
 * changes:
 * Guendouz, M. (2018). Room, LiveData, and RecyclerView. Medium. Available at:
 * https://medium.com/@guendouz/room-livedata-and-recyclerview-d8e96fb31dfe
 * [Accessed 26 Apr. 2026]
 *
 */

class ExpenseListActivity : AppCompatActivity() {

    private val TAG = "ExpenseListActivity"

    // Views (Android Developers, 2019)
    private lateinit var rvExpenses: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvTotalItems: TextView
    private lateinit var tvAvgPerDay: TextView

    // Adapter + DB
    private lateinit var adapter: ExpenseAdapter
    private lateinit var db: AppDatabase

    // The full list loaded from DB - filtering/sorting happens on this
    private var allExpenses: List<Expense> = emptyList()

    // Lifecycle (Android Developers, 2019)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        Log.d(TAG, "onCreate: ExpenseListActivity started")

        db = AppDatabase.getInstance(this)

        bindViews()
        setupRecyclerView()
        setupBottomNav()
        loadExpenses()
    }

    // Reload expenses every time the screen comes back into focus
    // (Android Developers, 2019)
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: reloading expenses")
        loadExpenses()
    }

    // Setup

    /** Binds all views by ID (GeeksforGeeks, 2025)*/
    private fun bindViews() {
        rvExpenses       = findViewById(R.id.rvExpenses)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        tvTotalExpenses  = findViewById(R.id.tvTotalExpenses)
        tvTotalItems     = findViewById(R.id.tvTotalItems)
        tvAvgPerDay      = findViewById(R.id.tvAvgPerDay)
    }

    /** Sets up RecyclerView with the ExpenseAdapter */
    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(emptyList()) { expense ->
            // Receipt tapped - open viewer
            openReceiptViewer(expense)
        }
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter
        Log.d(TAG, "setupRecyclerView: RecyclerView ready")
    }

    // Data loading

    /**
     * Loads expenses from Room DB for the logged-in user.
     */
    private fun loadExpenses() {
        lifecycleScope.launch {
            try {
                // Read the logged-in user's ID from SharedPreferences
                val prefs  = getSharedPreferences("BudgetBeePrefs", MODE_PRIVATE)
                val userId = prefs.getInt("loggedInUserId", -1)
                Log.d(TAG, "loadExpenses: loading for userId=$userId")

                // Fetch from DB
                val expenses = if (userId != -1) {
                    db.expenseDao().getExpensesForUser(userId)
                } else {
                    db.expenseDao().getAllExpenses()
                }

                allExpenses = expenses
                Log.d(TAG, "loadExpenses: found ${expenses.size} expenses")

                // Refresh the list and stats
                displayExpenses(allExpenses)

            } catch (e: Exception) {
                Log.e(TAG, "loadExpenses: error — ${e.message}", e)
            }
        }
    }

    /**
     * Pushes a list of expenses to the adapter and updates the stats header.
     * Also toggles the empty state view. (Medium, 2018)
     */
    private fun displayExpenses(expenses: List<Expense>) {
        adapter.updateList(expenses)

        // Toggle empty state
        if (expenses.isEmpty()) {
            rvExpenses.visibility       = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
            Log.d(TAG, "displayExpenses: showing empty state")
        } else {
            rvExpenses.visibility       = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
        }

        updateSummaryStats(expenses)
    }

    /**
     * Calculates and updates the three stats in the honey header card:
     * total spent, number of items, and average spend per day.
     */
    private fun updateSummaryStats(expenses: List<Expense>) {
        // (StackOverflow, 2019)
        val total      = expenses.sumOf { it.amount }
        val count      = expenses.size
        val uniqueDays = expenses.map { it.date }.distinct().size
        val avgPerDay  = if (uniqueDays > 0) total / uniqueDays else 0.0

        tvTotalExpenses.text = "R%.0f".format(total)
        tvTotalItems.text    = count.toString()
        tvAvgPerDay.text     = "R%.0f".format(avgPerDay)

        Log.d(TAG, "updateSummaryStats: total=R$total, items=$count, avg=R$avgPerDay")
    }

    // Receipt viewer

    /**
     * Opens the receipt photo for the given expense.
     * Passes the photo file path to ReceiptViewActivity. (Android Developers, 2019)
     */
    private fun openReceiptViewer(expense: Expense) {
        if (expense.receiptPhotoPath.isNullOrEmpty()) return
        Log.d(TAG, "openReceiptViewer: opening receipt for '${expense.description}'")
        val intent = Intent(this, ReceiptViewActivity::class.java)
        intent.putExtra("photoPath", expense.receiptPhotoPath)
        startActivity(intent)
    }

    // Bottom navigation
    private fun setupBottomNav() {

        // Home
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            Log.d(TAG, "navHome clicked")
            finish()
        }

        // Expenses
        findViewById<LinearLayout>(R.id.navExpenses).setOnClickListener {
            Log.d(TAG, "navExpenses clicked — already on this screen")
        }

        // Add Expense (+)
        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            Log.d(TAG, "fabAddExpense clicked")
            // startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // Goals
        findViewById<LinearLayout>(R.id.navGoals).setOnClickListener {
            Log.d(TAG, "navGoals clicked")
            // startActivity(Intent(this, GoalsActivity::class.java))
        }

        // Badges
        findViewById<LinearLayout>(R.id.navBadges).setOnClickListener {
            Log.d(TAG, "navBadges clicked")
            // startActivity(Intent(this, BadgesActivity::class.java))
        }
    }
}