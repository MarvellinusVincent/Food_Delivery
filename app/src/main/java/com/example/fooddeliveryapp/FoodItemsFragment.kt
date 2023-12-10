package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.databinding.FragmentAllRestaurantsBinding
import com.example.fooddeliveryapp.databinding.FragmentFoodItemsBinding

class FoodItemsFragment : Fragment() {

    private var _binding: FragmentFoodItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFoodItemsBinding.inflate(inflater, container, false)
        val view = binding.root
        val recyclerView = binding.foodItemsRecyclerView
        val foodItemsAdapter = FoodItemsAdapter()
        recyclerView.adapter = foodItemsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch and set food items from the ViewModel
        viewModel.restaurant.observe(viewLifecycleOwner) { restaurant ->
            restaurant?.let {
                val foodItems = listOf(
                    FoodItem(restaurant.restaurantName,"Pizza", "$10", 0, 10.0),
                    FoodItem(restaurant.restaurantName,"Burger", "$3", 0, 3.0),
                    FoodItem(restaurant.restaurantName,"Sushi", "$6", 0, 6.0),
                    FoodItem(restaurant.restaurantName,"Fries", "$2", 0, 2.0),
                    FoodItem(restaurant.restaurantName,"Chicken", "$4", 0, 4.0),
                    FoodItem(restaurant.restaurantName,"Pasta", "$8", 0, 8.0),
                    FoodItem(restaurant.restaurantName,"Mac & Cheese", "$4", 0, 4.0)
                )
                foodItemsAdapter.setFoodItems(foodItems)
            }
        }

        binding.checkOutButton.setOnClickListener {
            val selectedItems = foodItemsAdapter.getSelectedItems()
            for (item in selectedItems) {
                item.individualPrice = calculateIndividualPrice(item)
            }
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedItems", ArrayList(selectedItems))
            }
            view.findNavController().navigate(R.id.action_restaurantFragment_to_checkoutFragment, bundle)
        }

        return view
    }

    private fun calculateIndividualPrice(item: FoodItem): Double {
        val price = item.price.removePrefix("$").toDouble()
        return item.quantity * price
    }


    // Nested FoodItemsAdapter class
    inner class FoodItemsAdapter : RecyclerView.Adapter<FoodItemsAdapter.FoodItemViewHolder>() {

        private var foodItems: List<FoodItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
            return FoodItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
            val foodItem = foodItems[position]
            holder.bind(foodItem)
            holder.itemView.findViewById<ImageButton>(R.id.addFoodButton).setOnClickListener {
                foodItem.quantity++
                notifyItemChanged(position)
            }
            holder.itemView.findViewById<ImageButton>(R.id.deleteFoodButton).setOnClickListener {
                if (foodItem.quantity > 0) {
                    foodItem.quantity--
                    notifyItemChanged(position)
                }
            }
        }

        override fun getItemCount(): Int = foodItems.size

        fun setFoodItems(items: List<FoodItem>) {
            foodItems = items
            notifyDataSetChanged()
        }

        fun getSelectedItems(): List<FoodItem> {
            return foodItems.filter { it.quantity > 0 }
        }

        inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(foodItem: FoodItem) {
                itemView.findViewById<TextView>(R.id.foodItemName).text = foodItem.name
                itemView.findViewById<TextView>(R.id.foodItemPrice).text = foodItem.price
                itemView.findViewById<TextView>(R.id.quantityText).text = foodItem.quantity.toString()
            }
        }
    }
}
