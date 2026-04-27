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
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
 * Used for loading Room database data asynchronously using coroutines (lifecycleScope):
 * David (2021). Using coroutines with Android Room database. Stack Overflow. Available at:
 * https://stackoverflow.com/questions/68126665/using-coroutines-with-android-room-database.
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
 * Used for implementing data filtering logic:
 * Meyta Taliti (2022). Simple List with Date Range Filter - Meyta Taliti - Medium. Medium. Available at:
 * https://medium.com/@meytataliti/simple-list-with-date-range-filter-19bd71761495.
 * [Accessed 27 Apr. 2026]
 *
 * user1061793 (2012). How to add days into the date in android. Stack Overflow. Available at:
 * https://stackoverflow.com/questions/8738369/how-to-add-days-into-the-date-in-android.
 * [Accessed 27 Apr. 2026]
 *
 * Used for implementing toggle-based sorting logic in the RecyclerView:
 * Singh, P. (2021). How to sort reccyclerview in kotlin android. Stack Overflow. Available at:
 * https://stackoverflow.com/questions/67858149/how-to-sort-reccyclerview-in-kotlin-android.
 * [Accessed 27 Apr. 2026]
 *
 *
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

    // Tracks current sort order - true = newest first (default)
    private var isSortedNewest = true

    // Tracks which chip is active - default is "This Month"
    private var activeChip = "THIS_MONTH"

    // Custom date range
    private var customStartDate: String? = null
    private var customEndDate: String?   = null

    // Lifecycle (Android Developers, 2019)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        Log.d(TAG, "onCreate: ExpenseListActivity started")

        db = AppDatabase.getInstance(this)

        bindViews()
        setupRecyclerView()
        setupBottomNav()
        setupSortButtons()
        setupChipFilters()
        setupDateRangePickers()
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

    /**
     * Sets up the Newest First / Oldest First sort toggle buttons
     */
    private fun setupSortButtons() {
        val btnSortDesc = findViewById<CardView>(R.id.btnSortDesc)
        val btnSortAsc = findViewById<CardView>(R.id.btnSortAsc)
        val tvSortDesc = findViewById<TextView>(R.id.tvSortDesc)
        val tvSortAsc = findViewById<TextView>(R.id.tvSortAsc)

        // Newest First button clicked
        btnSortDesc.setOnClickListener {
            if (!isSortedNewest) {
                isSortedNewest = true
                Log.d(TAG, "Sort changed to: Newest First")

                // Highlight Newest button, dim Oldest button
                btnSortDesc.setCardBackgroundColor(getColor(R.color.honey))
                btnSortAsc.setCardBackgroundColor(getColor(R.color.white))
                tvSortDesc.setTextColor(getColor(R.color.black_deep))
                tvSortAsc.setTextColor(getColor(R.color.muted_text))

                applyCurrentFiltersAndSort()
            }
        }

        // Oldest First button clicked
        btnSortAsc.setOnClickListener {
            if (isSortedNewest) {
                isSortedNewest = false
                Log.d(TAG, "Sort changed to: Oldest First")

                // Highlight Oldest button, dim Newest button
                btnSortAsc.setCardBackgroundColor(getColor(R.color.honey))
                btnSortDesc.setCardBackgroundColor(getColor(R.color.white))
                tvSortAsc.setTextColor(getColor(R.color.black_deep))
                tvSortDesc.setTextColor(getColor(R.color.muted_text))

                applyCurrentFiltersAndSort()
            }
        }
    }

    // Chip filter setup

    /**
     * Sets up the three quick-filter chips: This Month, Last 7 Days, 3 Months.
     * Each chip sets the active date range and refreshes the list.
     */
    private fun setupChipFilters() {
        val chipThisMonth = findViewById<TextView>(R.id.chipThisMonth)
        val chipLast7     = findViewById<TextView>(R.id.chipLast7)
        val chip3Months   = findViewById<TextView>(R.id.chip3Months)

        chipThisMonth.setOnClickListener {
            activeChip      = "THIS_MONTH"
            customStartDate = null
            customEndDate   = null
            Log.d(TAG, "Chip selected: This Month")
            updateChipStyles(chipThisMonth, chipLast7, chip3Months)
            applyCurrentFiltersAndSort()
        }

        chipLast7.setOnClickListener {
            activeChip      = "LAST_7"
            customStartDate = null
            customEndDate   = null
            Log.d(TAG, "Chip selected: Last 7 Days")
            updateChipStyles(chipLast7, chipThisMonth, chip3Months)
            applyCurrentFiltersAndSort()
        }

        chip3Months.setOnClickListener {
            activeChip      = "3_MONTHS"
            customStartDate = null
            customEndDate   = null
            Log.d(TAG, "Chip selected: 3 Months")
            updateChipStyles(chip3Months, chipThisMonth, chipLast7)
            applyCurrentFiltersAndSort()
        }
    }

    /**
     * Updates chip visual styles
     * Active chip gets honey background, inactive chips get grey style
     */
    private fun updateChipStyles(active: TextView, vararg inactive: TextView) {
        active.setBackgroundResource(R.drawable.tab_active_bg)
        active.setTextColor(getColor(R.color.black_deep))
        inactive.forEach { chip ->
            chip.setBackgroundResource(R.drawable.tab_row_bg)
            chip.setTextColor(getColor(R.color.muted_text))
        }
    }

    // Custom date range pickers

    /**
     * Sets up the Start Date and End Date Material date picker buttons.
     */
    private fun setupDateRangePickers() {
        val btnStartDate = findViewById<MaterialButton>(R.id.btnStartDate)
        val btnEndDate   = findViewById<MaterialButton>(R.id.btnEndDate)

        btnStartDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select start date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                val sdf  = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = sdf.format(Date(selection))
                customStartDate   = date
                btnStartDate.text = "📅 $date"
                Log.d(TAG, "Start date selected: $date")

                // Auto-apply filter if end date already set
                if (customEndDate != null) {
                    clearChipSelection()
                    applyCurrentFiltersAndSort()
                }
            }

            picker.show(supportFragmentManager, "START_DATE_PICKER")
        }

        btnEndDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("Select end date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                val sdf  = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = sdf.format(Date(selection))
                customEndDate   = date
                btnEndDate.text = "📅 $date"
                Log.d(TAG, "End date selected: $date")

                // Auto-apply filter if start date already set
                if (customStartDate != null) {
                    clearChipSelection()
                    applyCurrentFiltersAndSort()
                }
            }

            picker.show(supportFragmentManager, "END_DATE_PICKER")
        }
    }

    /**
     * Clears the visual highlight from all chips when a custom date range is applied instead.
     */
    private fun clearChipSelection() {
        activeChip = "CUSTOM"
        val chipThisMonth = findViewById<TextView>(R.id.chipThisMonth)
        val chipLast7     = findViewById<TextView>(R.id.chipLast7)
        val chip3Months   = findViewById<TextView>(R.id.chip3Months)
        listOf(chipThisMonth, chipLast7, chip3Months).forEach { chip ->
            chip.setBackgroundResource(R.drawable.tab_row_bg)
            chip.setTextColor(getColor(R.color.muted_text))
        }
        Log.d(TAG, "Chip selection cleared - using custom date range")
    }

    // Filter and Sort
    /**
     * Central function that applies the current sort order to allExpenses
     * (StackOverflow, 2021)
     */
        private fun applyCurrentFiltersAndSort() {
        // Determine date range from chip or custom dates (Medium, 2022)
        val filtered = if (customStartDate != null && customEndDate != null) {
            // Custom date range takes priority over chips
            filterByDateRange(allExpenses, customStartDate!!, customEndDate!!)
        } else {
            // Use the active chip range (Medium, 2022)
            val (start, end) = getChipDateRange(activeChip)
            filterByDateRange(allExpenses, start, end)
        }

        // Sort the filtered list
        val sorted = if (isSortedNewest) {
            filtered.sortedByDescending { it.date }
        } else {
            filtered.sortedBy { it.date }
        }

        Log.d(TAG, "applyCurrentFiltersAndSort: ${sorted.size} expenses after filter+sort")
        displayExpenses(sorted)
    }

    /**
     * Returns start and end date strings for the given chip key
     */
    private fun getChipDateRange(chip: String): Pair<String, String> {
        val sdf   = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val today = java.util.Calendar.getInstance()
        // Calendar usage for dynamic date manipulation (StackOverflow, 2012)
        val end   = sdf.format(today.time)

        val start = when (chip) {
            "LAST_7" -> {
                // Adding/subtracting days using Calendar (StackOverflow, 2012)
                today.add(java.util.Calendar.DAY_OF_YEAR, -7)
                sdf.format(today.time)
            }
            "3_MONTHS" -> {
                // Calendar.MONTH manipulation for range shifts (StackOverflow, 2012)
                today.add(java.util.Calendar.MONTH, -3)
                sdf.format(today.time)
            }
            else -> {
                // THIS_MONTH - from the 1st of the current month
                today.set(java.util.Calendar.DAY_OF_MONTH, 1)
                sdf.format(today.time)
            }
        }
        return Pair(start, end)
    }

    /**
     * Filters expenses to only those within the given date range (inclusive).
     */
    private fun filterByDateRange(
        expenses: List<Expense>,
        start: String,
        end: String
    ): List<Expense> {
        val sdf       = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        val startDate = sdf.parse(start) ?: return expenses
        val endDate   = sdf.parse(end)   ?: return expenses

        return expenses.filter { expense ->
            val expenseDate = sdf.parse(expense.date)
            // Core filtering logic based on date comparison
            expenseDate != null && !expenseDate.before(startDate) && !expenseDate.after(endDate)
        }
    }

    // Data loading

    /**
     * Loads expenses from Room DB for the logged-in user
     */
    private fun loadExpenses() {
        // (StackOverflow, 2021)
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

                applyCurrentFiltersAndSort()

            } catch (e: Exception) {
                Log.e(TAG, "loadExpenses: error - ${e.message}", e)
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
            Log.d(TAG, "navExpenses clicked - already on this screen")
        }

        // Add Expense (+)
        findViewById<CardView>(R.id.fabAddExpense).setOnClickListener {
            Log.d(TAG, "fabAddExpense clicked")
             startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // Goals
        findViewById<LinearLayout>(R.id.navGoals).setOnClickListener {
            Log.d(TAG, "navGoals clicked")
             startActivity(Intent(this, GoalsActivity::class.java))
        }

        // Badges
        findViewById<LinearLayout>(R.id.navBadges).setOnClickListener {
            Log.d(TAG, "navBadges clicked")
            startActivity(Intent(this, BadgesActivity::class.java))
        }
    }
}