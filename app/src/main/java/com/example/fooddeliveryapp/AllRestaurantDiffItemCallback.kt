package com.example.fooddeliveryapp

import androidx.recyclerview.widget.DiffUtil

/**
 * DiffUtil ItemCallback for comparing [Restaurant] objects in a RecyclerView.
 *
 * This class is used by the RecyclerView adapter to efficiently update the UI when the
 * underlying data set of restaurants changes.
 *
 * @property oldItem The old [Restaurant] item to compare.
 * @property newItem The new [Restaurant] item to compare.
 */
class AllRestaurantDiffItemCallback : DiffUtil.ItemCallback<Restaurant>() {

    /**
     * Checks if the unique identifiers of the old and new [Restaurant] items are the same.
     *
     * @param oldItem The old [Restaurant] item.
     * @param newItem The new [Restaurant] item.
     * @return True if the unique identifiers are the same, false otherwise.
     */
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.restaurantName == newItem.restaurantName
    }

    /**
     * Checks if the contents of the old and new [Restaurant] items are the same.
     *
     * @param oldItem The old [Restaurant] item.
     * @param newItem The new [Restaurant] item.
     * @return True if the contents are the same, false otherwise.
     */
    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}
