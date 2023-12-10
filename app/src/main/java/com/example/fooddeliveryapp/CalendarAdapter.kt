package com.example.fooddeliveryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CalendarAdapter(
    context: Context,
    resource: Int,
    objects: List<String>,
    private val orderDates: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false)

        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)

        val dayString = getItem(position)
        dayTextView.text = dayString

        // You can customize this logic based on your needs
        iconImageView.visibility = if (dayString in orderDates) {
            View.VISIBLE
        } else {
            View.GONE
        }

        return view
    }
}
