package com.example.fooddeliveryapp

import androidx.recyclerview.widget.DiffUtil

class AllRestaurantDiffItemCallback : DiffUtil.ItemCallback<Restaurant>() {

    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.restaurantName == newItem.restaurantName
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}