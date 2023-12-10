package com.example.fooddeliveryapp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.DeliveryViewModel
import com.example.fooddeliveryapp.Restaurant
import com.example.fooddeliveryapp.SimpleLocation
import com.example.fooddeliveryapp.FoodItem
import com.example.fooddeliveryapp.Order
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        // Set up the ViewPager with the sections adapter.
        val sectionsPagerAdapter = SectionPageAdapter(childFragmentManager)
        sectionsPagerAdapter.addFragment(MonthlyFragment(), "Monthly View")
        sectionsPagerAdapter.addFragment(WeeklyFragment(), "Weekly View")

        viewPager.adapter = sectionsPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}
