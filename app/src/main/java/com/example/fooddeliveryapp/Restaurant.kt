package com.example.fooddeliveryapp

import android.location.Location
import android.widget.ImageView
import com.google.firebase.database.Exclude

data class Restaurant(
    @get:Exclude
    var restaurantName: String = "",
    var firstImage: Int = 0,
    var secondImage: Int = 0,
    var thirdImage: Int = 0,
    var location: SimpleLocation = SimpleLocation(0.0, 0.0)
)