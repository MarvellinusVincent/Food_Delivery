package com.example.fooddeliveryapp
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeeklyFragment : Fragment() {

    private lateinit var weeklyBarChart: BarChart
    private val viewModel: DeliveryViewModel by activityViewModels()
    private lateinit var btnPreviousWeek: ImageButton
    private lateinit var btnNextWeek: ImageButton

    /**
     * Called when the fragment's view is created.
     *
     * @param inflater The LayoutInflater object to inflate views.
     * @param container The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weekly, container, false)
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart)

        btnPreviousWeek = view.findViewById(R.id.prevWeekButton)
        btnNextWeek = view.findViewById(R.id.nextWeekButton)

        btnPreviousWeek.setOnClickListener {
            navigateToPreviousWeek()
        }

        btnNextWeek.setOnClickListener {
            navigateToNextWeek()
        }

        /** Set up your BarChart with data */
        setupWeeklyBarChart()

        return view
    }

    private fun setupWeeklyBarChart() {
        val calendar = Calendar.getInstance()

        /** Get the current week's start and end dates */
        val startDate = viewModel.getCurrentStartDate()
        calendar.time = startDate
        val endDate = getEndOfWeek(calendar)

        /** Retrieve weekly spending data from ViewModel */
        val weeklySpendingData = viewModel.getWeeklySpending(startDate, endDate)

        /** Prepare data for BarChart */
        val entries = ArrayList<BarEntry>()
        val dateLabels = ArrayList<String>()

        /** Iterate over each day of the week */
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            val currentDate = calendar.time
            val dailySpending = weeklySpendingData.find { viewModel.isSameDate(it.date, currentDate) }

            /** If there are orders for the current day, add the spending amount to the chart */
            if (dailySpending != null) {
                val spendingAmount = dailySpending.price.toFloat()
                entries.add(BarEntry(i.toFloat(), spendingAmount))
            } else {
            /** If no orders for the current day, set the bar height to zero */
                entries.add(BarEntry(i.toFloat(), 0f))
            }

            dateLabels.add(getFormattedDate(currentDate))
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        val dataSet = BarDataSet(entries, "Money Spend")
        dataSet.valueTextColor = resources.getColor(android.R.color.black)
        dataSet.valueTextSize = 12f

        val xAxis = weeklyBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)

        val yAxis = weeklyBarChart.axisLeft
        yAxis.axisMinimum = 0f

        val barData = BarData(dataSet)
        weeklyBarChart.data = barData
        weeklyBarChart.description.isEnabled = false
        weeklyBarChart.isClickable = true
        weeklyBarChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    val dayOfWeek = it.x.toInt()
                    val selectedDate = calendar.apply { set(Calendar.DAY_OF_WEEK, dayOfWeek) }.time
                    Log.d("Weekly fragment", "Selected date: $selectedDate")
                    val selectedOrders = viewModel.getOrdersForDate(selectedDate)
                    Log.d("Weekly fragment", "Selected orders: $selectedOrders")

                    if (selectedOrders.isNotEmpty()) {
                        showPopupWindow(selectedOrders)
                    } else {
                        Log.d("Weekly fragment", "not popped up")
                    }
                }
            }

            override fun onNothingSelected() {
            }
        })

        weeklyBarChart.invalidate()
    }

    private fun getEndOfWeek(calendar: Calendar): Date {
        val currentDate = Calendar.getInstance()
        currentDate.time = calendar.time

        /** Set the calendar to the end of the week */
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        currentDate.add(Calendar.DAY_OF_WEEK, 1)

        return currentDate.time
    }

    private fun getFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun navigateToPreviousWeek() {
        /** Adjust the calendar to the previous week */
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.getCurrentStartDate()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)

        /** Update the ViewModel with the new start date */
        viewModel.setCurrentStartDate(calendar.time)

        /** Refresh the BarChart with the new data */
        setupWeeklyBarChart()
    }

    private fun navigateToNextWeek() {
        /** Adjust the calendar to the next week */
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.getCurrentStartDate()
        calendar.add(Calendar.WEEK_OF_YEAR, 1)

        /** Update the ViewModel with the new start date */
        viewModel.setCurrentStartDate(calendar.time)

        /** Refresh the BarChart with the new data */
        setupWeeklyBarChart()
    }

    private fun showPopupWindow(orders: List<Order>) {
        /** Customize the appearance and content of the pop-up window */
        val popupView = layoutInflater.inflate(R.layout.popup_layout, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        /** Set up UI components in the pop-up window */
        val totalSpendingTextView: TextView = popupView.findViewById(R.id.tvTotalMoneySpend)
        val restaurantNameTextView: TextView = popupView.findViewById(R.id.tvRestaurantName)

        /** Calculate total spending */
        val totalSpending = orders.sumByDouble { it.price.toDouble() }
        totalSpendingTextView.text = getString(R.string.total_money_spend, totalSpending)

        /** Get the name of the restaurant from the first order (assuming all orders have the same restaurant) */
        val restaurantName = orders.firstOrNull()?.restaurantName ?: ""
        restaurantNameTextView.text = getString(R.string.restaurant_name, restaurantName)

        /** Show the pop-up window at a specific location relative to the anchor view (e.g., the BarChart) */
        popupWindow.showAtLocation(weeklyBarChart, Gravity.CENTER, 0, 0)
    }
}
