package com.gia.poe_demo

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gia.poe_demo.databinding.ActivityRemindersBinding
import com.gia.poe_demo.databinding.ItemReminderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.android.material.chip.Chip

class RemindersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemindersBinding
    private lateinit var db: AppDatabase

    // Selected due date, stored as epoch ms
    private var selectedDateMillis: Long = 0L
    private var selectedDateDisplay: String = ""

    // Notify-me selection: 0 = on day, 1, 3, or 5
    private var notifyDaysBefore: Int = 0

    private val currencyFormat = NumberFormat.getNumberInstance(Locale("en", "ZA"))
    private val displayFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        setupNotifyChips()
        setupDatePicker()
        setupAddButton()
        observeReminders()
        setupNavigation()

        binding.tvBack.setOnClickListener { finish() }
    }


    private fun setupNotifyChips() {
        val chips = listOf(
            binding.chipOnDay  to 0,
            binding.chip1Day   to 1,
            binding.chip3Days  to 3,
            binding.chip5Days  to 5
        )
        chips.forEach { (chip, days) ->
            chip.setOnClickListener {
                notifyDaysBefore = days
                chips.forEach { (c, _) -> c.isSelected = false }
                chip.isSelected = true
                refreshChipStyles(chips)
            }
        }
        // Default: "On day" selected
        binding.chipOnDay.isSelected = true
        refreshChipStyles(chips)
    }

    private fun refreshChipStyles(chips: List<Pair<android.widget.TextView, Int>>) {
        chips.forEach { (chip, _) ->
            if (chip.isSelected) {
                chip.setBackgroundResource(R.drawable.tab_active_bg)
                chip.setTextColor(getColor(R.color.black_deep))
            } else {
                chip.setBackgroundResource(R.drawable.tab_row_bg)
                chip.setTextColor(getColor(R.color.muted_text))
            }
        }
    }

    // Date picker tied to the due-date field
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val pickDate = {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val cal = Calendar.getInstance().apply { set(year, month, day, 0, 0, 0) }
                    selectedDateMillis   = cal.timeInMillis
                    selectedDateDisplay  = displayFormat.format(cal.time)
                    binding.etReminderDate.setText(selectedDateDisplay)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply { datePicker.minDate = System.currentTimeMillis() }
                .show()
        }

        binding.etReminderDate.setOnClickListener { pickDate() }
        binding.tilReminderDate.setEndIconOnClickListener { pickDate() }
    }

    //  Save new reminder to Room
    private fun setupAddButton() {
        binding.btnAddReminder.setOnClickListener {
            val name   = binding.etReminderName.text.toString().trim()
            val amount = binding.etReminderAmount.text.toString().toDoubleOrNull()

            when {
                name.isBlank()         -> { toast("Please enter a reminder name"); return@setOnClickListener }
                amount == null || amount <= 0 -> { toast("Please enter a valid amount"); return@setOnClickListener }
                selectedDateMillis == 0L -> { toast("Please select a due date"); return@setOnClickListener }
            }

            val reminder = Reminder(
                name             = name,
                amount           = amount!!,
                dueDateMillis    = selectedDateMillis,
                dueDateDisplay   = selectedDateDisplay,
                notifyDaysBefore = notifyDaysBefore
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) { db.reminderDao().insert(reminder) }
                toast("Reminder added ✓")
                clearForm()
            }
        }
    }

    // Observe reminder list and build cards
    private fun observeReminders() {
        val nowMillis = System.currentTimeMillis()
        lifecycleScope.launch {
            db.reminderDao().getUpcomingReminders(nowMillis).collectLatest { reminders ->
                binding.reminderContainer.removeAllViews()

                if (reminders.isEmpty()) {
                    binding.tvNoReminders.visibility = View.VISIBLE
                } else {
                    binding.tvNoReminders.visibility = View.GONE
                    reminders.forEach { reminder -> addReminderCard(reminder) }
                }
            }
        }
    }

    private fun addReminderCard(reminder: Reminder) {
        val cardBinding = ItemReminderBinding.inflate(
            LayoutInflater.from(this),
            binding.reminderContainer,
            false
        )

        val notifyLabel = when (reminder.notifyDaysBefore) {
            0    -> "on the day"
            1    -> "1 day before"
            3    -> "3 days before"
            5    -> "5 days before"
            else -> "${reminder.notifyDaysBefore} days before"
        }

        cardBinding.tvReminderTitle.text      = reminder.name
        cardBinding.tvReminderSubtitle.text   = "R ${currencyFormat.format(reminder.amount)} · ${reminder.dueDateDisplay}"
        cardBinding.tvNotifyLabel.text        = "🔔  Notify $notifyLabel"
        cardBinding.tvNotifyBadge.text        = notifyLabel
        cardBinding.btnDeleteReminder.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) { db.reminderDao().delete(reminder) }
                toast("Reminder deleted")
            }
        }

        binding.reminderContainer.addView(cardBinding.root)
    }

    // Helpers
    private fun clearForm() {
        binding.etReminderName.text?.clear()
        binding.etReminderAmount.text?.clear()
        binding.etReminderDate.text?.clear()
        selectedDateMillis  = 0L
        selectedDateDisplay = ""
        notifyDaysBefore    = 0
        binding.chipOnDay.isSelected = true
        refreshChipStyles(listOf(
            binding.chipOnDay  to 0,
            binding.chip1Day   to 1,
            binding.chip3Days  to 3,
            binding.chip5Days  to 5
        ))
    }

    private fun setupNavigation() {
        binding.navHome.setOnClickListener { finish() }
        binding.navExpenses.setOnClickListener { /* navigate */ }
        binding.fabAddExpense.setOnClickListener { /* navigate */ }
        binding.navGoals.setOnClickListener { /* navigate */ }
        binding.navBadges.setOnClickListener { /* navigate */ }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
