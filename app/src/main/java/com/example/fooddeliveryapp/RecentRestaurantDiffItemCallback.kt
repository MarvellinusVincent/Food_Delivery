package com.example.fooddeliveryapp

import androidx.recyclerview.widget.DiffUtil

class RecentRestaurantDiffItemCallback : DiffUtil.ItemCallback<Order>() {

    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.restaurantName == newItem.restaurantName
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}