package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.databinding.FragmentRecentOrdersBinding

/**
 * Fragment responsible for displaying the user's recent orders.
 */
class RecentOrdersFragment : Fragment() {

    private var _binding: FragmentRecentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity).setToolbarVisibility(true)
        _binding = FragmentRecentOrdersBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.recyclerViewRecentOrders
        val ordersAdapter = RecentOrdersAdapter()
        recyclerView.adapter = ordersAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        /** Fetch recent orders from the database and set them to the adapter */
        val allOrders = viewModel.getAllOrders()
        Log.d("Recent Order Fragment", "$allOrders")
        ordersAdapter.setOrders(allOrders)
        return view
    }

    inner class RecentOrdersAdapter : RecyclerView.Adapter<RecentOrdersAdapter.RecentOrderViewHolder>() {

        private var orders: List<Order> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_details_with_order_again_button, parent, false)
            return RecentOrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecentOrderViewHolder, position: Int) {
            val order = orders[position]
            holder.bind(order)
        }

        override fun getItemCount(): Int = orders.size

        /**
         * Set the list of recent orders for the adapter and notify any observers of the data set change.
         *
         * @param orderList List of [Order] objects representing recent orders.
         */
        fun setOrders(orderList: List<Order>) {
            orders = orderList
            notifyDataSetChanged()
        }

        inner class RecentOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            /**
             * Bind recent order details to the corresponding UI elements in the order_item layout.
             *
             * @param order The [Order] object containing details to be displayed.
             */
            fun bind(order: Order) {
                /** Bind order details to UI elements */
                itemView.findViewById<TextView>(R.id.OrderAgainDate).text = "Date: ${order.getFormattedDate()}"
                itemView.findViewById<TextView>(R.id.OrderAgainTime).text = "Time: ${order.time}"
                itemView.findViewById<TextView>(R.id.OrderAgainPrice).text = "Price: ${order.price}"
                itemView.findViewById<TextView>(R.id.OrderAgainRestaurantName).text = "Ordered from: ${order.restaurantName}"
                itemView.findViewById<TextView>(R.id.OrderAgainAddress).text = "Delivery Address: ${order.deliveryAddress}"

                /** Create a LinearLayout to hold food items and quantities */
                val foodItemsLayout = itemView.findViewById<LinearLayout>(R.id.foodItemsOrderAgainLayout)

                /** Clear any previous views in the layout */
                foodItemsLayout.removeAllViews()

                /** Iterate through the list of food items in the order */
                for (foodItem in order.foodName.split(", ")) {
                    val foodItemView = LayoutInflater.from(itemView.context).inflate(R.layout.food_item_row, null)
                    val itemNameTextView = foodItemView.findViewById<TextView>(R.id.foodItemRowName)
                    val quantityTextView = foodItemView.findViewById<TextView>(R.id.foodItemRowQuantity)

                    /** Set the name and quantity for each food item */
                    itemNameTextView.text = foodItem
                    quantityTextView.text = "Quantity: ${order.quantity}"

                    /** Add the food item view to the layout */
                    foodItemsLayout.addView(foodItemView)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
