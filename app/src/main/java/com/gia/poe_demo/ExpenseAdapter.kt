package com.gia.poe_demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView adapter for the Expense List screen.
 * RecyclerView implementation based on:
 * Android Developers (2026). Create dynamic lists with RecyclerView. Android Developers. Available at:
 * https://developer.android.com/develop/ui/views/layout/recyclerview.
 * [Accessed 26 Apr. 2026]
 *
 * GeeksforGeeks (2025). RecyclerView in Android with Example. GeeksforGeeks. Available at:
 * https://www.geeksforgeeks.org/android/android-recyclerview/.
 * [Accessed 26 Apr.  2026]
 */

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onReceiptClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder (GeeksforGeeks, 2025)
    // Holds references to all UI components in a single expense item for recycling
    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView    = itemView.findViewById(R.id.tvExpenseDescription)
        val tvCategoryTime: TextView   = itemView.findViewById(R.id.tvExpenseCategoryTime)
        val tvAmount: TextView         = itemView.findViewById(R.id.tvExpenseAmount)
        val tvDate: TextView           = itemView.findViewById(R.id.tvExpenseDate)
        val tvReceiptLink: TextView    = itemView.findViewById(R.id.tvReceiptLink)
        val tvCategoryIcon: TextView   = itemView.findViewById(R.id.tvCategoryIcon)
    }

    // Adapter overrides (GeeksforGeeks, 2025)
    // Handles creation of item views, binding data, and determining list size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int = expenses.size

    // Binds expense data (description, amount, date, category) to UI elements for display
    // (GeeksforGeeks, 2025)
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // Basic fields
        holder.tvDescription.text = expense.description
        holder.tvAmount.text      = "-R %.2f".format(expense.amount)
        holder.tvDate.text        = expense.date

        // Category with time row show "Uncategorised" if empty
        val cat  = expense.category.ifEmpty { "Uncategorised" }
        val time = expense.startTime.ifEmpty { "" }
        holder.tvCategoryTime.text = if (time.isNotEmpty()) "$cat · $time" else cat

        // Emoji icon based on category name
        holder.tvCategoryIcon.text = getCategoryEmoji(cat)

        // Receipt link - only visible when a photo path is stored
        if (!expense.receiptPhotoPath.isNullOrEmpty()) {
            holder.tvReceiptLink.visibility = View.VISIBLE
            holder.tvReceiptLink.setOnClickListener { onReceiptClick(expense) }
        } else {
            holder.tvReceiptLink.visibility = View.GONE
        }
    }

    // Public helpers

    /**
     * Replaces the current list and refreshes the RecyclerView.
     * Called every time a filter or sort changes.
     */
    fun updateList(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }

    // Private helpers

    /**
     * Maps a category name to a matching emoji for the icon block.
     */
    private fun getCategoryEmoji(category: String): String {
        return when (category.trim().lowercase()) {
            "groceries"              -> "🛒"
            "transport", "uber",
            "taxi", "bus"            -> "🚗"
            "dining out", "food",
            "restaurant", "takeaway" -> "🍔"
            "entertainment",
            "cinema", "movies"       -> "🎬"
            "health", "pharmacy",
            "medical"                -> "💊"
            "shopping", "clothes"    -> "🛍️"
            "fuel", "petrol"         -> "⛽"
            "utilities", "bills"     -> "💡"
            else                     -> "📝"
        }
    }
}