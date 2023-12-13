package com.example.fooddeliveryapp

import com.google.firebase.database.Exclude

/**
 * Data class representing a restaurant.
 *
 * @property restaurantName The name of the restaurant.
 * @property firstImage The resource ID of the first image associated with the restaurant.
 * @property secondImage The resource ID of the second image associated with the restaurant.
 * @property thirdImage The resource ID of the third image associated with the restaurant.
 * @property location The location of the restaurant represented by [SimpleLocation].
 */
data class Restaurant(
    @get:Exclude
    var restaurantName: String = "",
    var firstImage: Int = 0,
    var secondImage: Int = 0,
    var thirdImage: Int = 0,
    var location: SimpleLocation = SimpleLocation(0.0, 0.0)
) {
    // Additional methods or properties can be added as needed
}
