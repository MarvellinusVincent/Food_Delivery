package com.example.fooddeliveryapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Adapter for managing fragments within a ViewPager.
 *
 * @param fm The FragmentManager that will interact with this adapter.
 */
class SectionPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()

    /**
     * Add a fragment to the adapter.
     *
     * @param fragment The Fragment to be added.
     * @param title The title associated with the Fragment.
     */
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    /**
     * Retrieve the fragment at the specified position.
     *
     * @param position The position of the fragment.
     * @return The Fragment at the specified position.
     */
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    /**
     * Get the number of fragments managed by the adapter.
     *
     * @return The total number of fragments.
     */
    override fun getCount(): Int {
        return fragmentList.size
    }

    /**
     * Get the title of the fragment at the specified position.
     *
     * @param position The position of the fragment.
     * @return The title of the fragment.
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
}
