package com.example.fooddeliveryapp

/**
 * Data class representing a user in the food delivery application.
 *
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property profilePictureUri The URI of the user's profile picture (nullable).
 */
data class User(
    var name: String = "",
    var email: String = "",
    var profilePictureUri: String? = null
)
