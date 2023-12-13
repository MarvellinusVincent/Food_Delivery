package com.example.fooddeliveryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment representing the monthly view of the calendar.
 */
class MonthlyFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var calendarAdapter: ArrayAdapter<String>
    private lateinit var monthYearTextView: TextView
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).setToolbarVisibility(true)
        val view = inflater.inflate(R.layout.fragment_monthly, container, false)

        gridView = view.findViewById(R.id.gridView)
        monthYearTextView = view.findViewById(R.id.monthYearTextView)
        val prevMonthButton: ImageButton = view.findViewById(R.id.prevMonthButton)
        val nextMonthButton: ImageButton = view.findViewById(R.id.nextMonthButton)
        val totalPriceTextView: TextView = view.findViewById(R.id.totalPriceTextView)

        /** Initialize the calendar */
        val calendar = Calendar.getInstance()
        updateMonthYearText(calendar)

        /** Set up the grid view */
        calendarAdapter = CalendarAdapter(requireContext(), R.layout.item_calendar_day, mutableListOf(), viewModel.getCalendarOrderDates())
        gridView.adapter = calendarAdapter

        /** Set up item click listener */
        gridView.setOnItemClickListener { _, _, position, _ ->
        }

        /** Set up button click listeners */
        prevMonthButton.setOnClickListener { navigateMonth(-1) }
        nextMonthButton.setOnClickListener { navigateMonth(1) }

        val orderDates = viewModel.getAllOrderDates()
        updateCalendar(calendar, orderDates)

        gridView.setOnItemClickListener { _, _, position, _ ->
            val dayString = calendarAdapter.getItem(position)
            val day = dayString?.toIntOrNull()

            if (day != null) {
                val calendarDay = Calendar.getInstance()
                calendarDay.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day)

                val ordersForDay = viewModel.getOrdersForDate(calendarDay.time)

                if (ordersForDay.isNotEmpty()) {
                    /** Calculate total price spent during that day */
                    val totalPrice = ordersForDay.sumOf { it.price.toDoubleOrNull() ?: 0.0 }

                    /** Display the total price below the calendar */
                    totalPriceTextView.text = "Total price spent for ${SimpleDateFormat("MMM d", Locale.getDefault()).format(calendarDay.time)}: $totalPrice"
                } else {
                    /** Clear the total price text when there are no orders for the selected day */
                    totalPriceTextView.text = ""
                }
            }
        }

        return view
    }

    /**
     * Updates the month and year text in the UI based on the given calendar.
     */
    private fun updateMonthYearText(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearTextView.text = dateFormat.format(calendar.time)
    }

    /**
     * Navigates to the previous or next month based on the provided offset.
     */
    private fun navigateMonth(offset: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            .parse(monthYearTextView.text.toString())!!

        calendar.add(Calendar.MONTH, offset)
        updateMonthYearText(calendar)
        val orderDates = viewModel.getAllOrderDates()
        updateCalendar(calendar, orderDates)
    }

    /**
     * Updates the calendar grid based on the provided calendar and order dates.
     */
    private fun updateCalendar(calendar: Calendar, orderDates: List<Date>) {
        calendarAdapter.clear()

        /** Set the calendar to the first day of the month */
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

        /** Add days from the previous month (colored light gray) */
        val lastDayOfPreviousMonth = calendar.clone() as Calendar
        lastDayOfPreviousMonth.add(Calendar.MONTH, -1)
        val daysInPreviousMonth = lastDayOfPreviousMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        val startDay = daysInPreviousMonth - (firstDayOfMonth - 2)
        for (day in startDay..daysInPreviousMonth) {
            calendarAdapter.add(day.toString())
        }

        /** Add days of the month (colored black) */
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            calendarAdapter.add(day.toString())
        }

        /** Add days from the next month (colored light gray) */
        val daysToAddFromNextMonth = 42 - (calendarAdapter.count % 42)
        for (day in 1..daysToAddFromNextMonth) {
            calendarAdapter.add(day.toString())
        }
        val monthDayFormatter = SimpleDateFormat("MMM d", Locale.getDefault())

        gridView.post {
            for (i in 0 until calendarAdapter.count) {
                val view = gridView.getChildAt(i)
                if (view != null) {
                    val dayString = calendarAdapter.getItem(i)
                    val day = dayString?.toIntOrNull()
                    val calendarDay = Calendar.getInstance()
                    calendarDay.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day!!)
                    val formattedCalendarDay = monthDayFormatter.format(calendarDay.time)
                    val color = if (formattedCalendarDay in orderDates.map { monthDayFormatter.format(it) }) {
                        /** Highlight days with orders */
                        resources.getColor(R.color.highlight_color)
                    } else {
                        /** Days of the current month (black) */
                        resources.getColor(android.R.color.white)
                    }
                    val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)
                    iconImageView.visibility = if (formattedCalendarDay in orderDates.map { monthDayFormatter.format(it) }) {
                        /** Show icon for days with orders */
                        View.VISIBLE
                    } else {
                        /** Hide icon for other days */
                        View.GONE
                    }
                    view.setBackgroundColor(color)
                }
            }
        }
    }
}
