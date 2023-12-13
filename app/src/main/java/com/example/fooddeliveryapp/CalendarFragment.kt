package com.example.fooddeliveryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

/**
 * Fragment for displaying a calendar with tabs for monthly and weekly views.
 */
class CalendarFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        /** Set up the ViewPager with the sections adapter. */
        val sectionsPagerAdapter = SectionPageAdapter(childFragmentManager)
        sectionsPagerAdapter.addFragment(MonthlyFragment(), "Monthly View")
        sectionsPagerAdapter.addFragment(WeeklyFragment(), "Weekly View")

        viewPager.adapter = sectionsPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}
