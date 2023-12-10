// OrdersFragment.kt
package com.example.fooddeliveryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView = binding.ordersRecyclerView
        val ordersAdapter = OrdersAdapter() // You need to create an adapter for orders
        recyclerView.adapter = ordersAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Call a function to fetch order details from the database and set them to the adapter
        val filteredOrders = viewModel.getOrders(viewModel.restaurantName)
        ordersAdapter.setOrders(filteredOrders)

        return view
    }

    inner class OrdersAdapter : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

        private var orders: List<Order> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.order_details, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.bind(order)
        }

        override fun getItemCount(): Int = orders.size

        fun setOrders(orderList: List<Order>) {
            orders = orderList
            notifyDataSetChanged()
        }

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(order: Order) {
                // Bind the order details to the corresponding UI elements in the order_item layout
                itemView.findViewById<TextView>(R.id.orderDetailsDate).text = "Date: ${order.getFormattedDate()}"
                itemView.findViewById<TextView>(R.id.orderDetailsTime).text = "Time: ${order.time}"
                itemView.findViewById<TextView>(R.id.orderDetailsPrice).text = "Price: ${order.price}"
                itemView.findViewById<TextView>(R.id.orderDetailsRestaurantName).text = "Ordered from: ${order.restaurantName}"
                itemView.findViewById<TextView>(R.id.orderDetailsAddress).text = "Delivery Address: ${order.deliveryAddress}"

                // Create a LinearLayout to hold food items and quantities
                val foodItemsLayout = itemView.findViewById<LinearLayout>(R.id.foodItemsLayout)

                // Clear any previous views in the layout
                foodItemsLayout.removeAllViews()

                // Iterate through the list of food items in the order
                for (foodItem in order.foodName.split(", ")) {
                    val foodItemView = LayoutInflater.from(itemView.context).inflate(R.layout.food_item_row, null)
                    val itemNameTextView = foodItemView.findViewById<TextView>(R.id.foodItemRowName)
                    val quantityTextView = foodItemView.findViewById<TextView>(R.id.foodItemRowQuantity)

                    // Set the name and quantity for each food item
                    itemNameTextView.text = foodItem
                    quantityTextView.text = "Quantity: ${order.quantity}"

                    // Add the food item view to the layout
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
