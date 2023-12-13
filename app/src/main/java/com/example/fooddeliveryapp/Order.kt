package com.example.fooddeliveryapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a food delivery order.
 *
 * @property restaurantName The name of the restaurant.
 * @property foodName The name of the food item.
 * @property price The price of the food item.
 * @property quantity The quantity of the food item.
 * @property date The date of the order.
 * @property time The time of the order.
 * @property deliveryAddress The delivery address for the order.
 */
data class Order(
    var restaurantName: String = "",
    var foodName: String = "",
    var price: String = "",
    var quantity: String = "",
    var date: Date? = null,
    var time: String? = null,
    var deliveryAddress: String = "",
) {
    /**
     * Gets the formatted date of the order in MM/dd/yyyy format.
     *
     * @return The formatted date as a string.
     */
    fun getFormattedDate(): String {
        date?.let {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            return dateFormat.format(it)
        }
        return ""
    }
}
