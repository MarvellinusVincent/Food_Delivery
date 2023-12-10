package com.example.fooddeliveryapp

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Order(
    var restaurantName: String = "",
    var foodName: String = "",
    var price: String = "",
    var quantity: String = "",
    var date: Date? = null,
    var time: String? = null,
    var deliveryAddress: String = "",
) {
    fun getFormattedDate(): String {
        date?.let {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            return dateFormat.format(it)
        }
        return ""
    }
}