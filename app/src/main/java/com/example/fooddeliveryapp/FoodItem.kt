package com.example.fooddeliveryapp

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class representing a food item in a restaurant's menu.
 *
 * @property restaurantName The name of the restaurant.
 * @property name The name of the food item.
 * @property price The price of the food item as a String.
 * @property quantity The quantity of the food item.
 * @property individualPrice The price of a single unit of the food item.
 */
data class FoodItem(
    var restaurantName: String,
    var name: String,
    var price: String,
    var quantity: Int,
    var individualPrice: Double
) : Parcelable {
    /**
     * Parcelable constructor used for creating an instance from a Parcel.
     */
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble()
    )

    /**
     * Writes the object's properties to a Parcel.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeInt(quantity)
        parcel.writeDouble(individualPrice)
    }

    /**
     * Describes the kinds of special objects contained in this Parcelable instance.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Creator for the Parcelable interface.
     */
    companion object CREATOR : Parcelable.Creator<FoodItem> {
        /**
         * Creates an instance of the Parcelable class from a Parcel.
         */
        override fun createFromParcel(parcel: Parcel): FoodItem {
            return FoodItem(parcel)
        }

        /**
         * Creates a new array of the Parcelable class.
         */
        override fun newArray(size: Int): Array<FoodItem?> {
            return arrayOfNulls(size)
        }
    }
}
