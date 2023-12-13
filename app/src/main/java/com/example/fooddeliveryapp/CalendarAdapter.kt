package com.example.fooddeliveryapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Custom ArrayAdapter for displaying calendar days with an optional icon for specific dates.
 *
 * @param context The context of the application.
 * @param resource The resource ID for the layout file representing each calendar item.
 * @param objects The list of calendar day strings to be displayed.
 * @param orderDates The list of dates with orders, indicating days with an associated icon.
 */
class CalendarAdapter(
    context: Context,
    resource: Int,
    objects: List<String>,
    private val orderDates: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    /**
     * Overrides the default getView method to customize the appearance of each calendar item.
     *
     * @param position The position of the item in the list.
     * @param convertView The recycled view to repopulate, if available.
     * @param parent The parent view group.
     * @return The customized view for the calendar item at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false)

        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val iconImageView: ImageView = view.findViewById(R.id.iconImageView)

        val dayString = getItem(position)
        dayTextView.text = dayString

        /** You can customize this logic based on your needs */
        iconImageView.visibility = if (dayString in orderDates) {
            View.VISIBLE
        } else {
            View.GONE
        }

        return view
    }
}
