package com.example.fooddeliveryapp

import android.os.Bundle
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
import com.example.fooddeliveryapp.databinding.FragmentFoodItemsBinding

/**
 * Fragment for displaying the list of food items in a restaurant.
 */
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

        /** Fetch and set food items from the ViewModel */
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

    /**
     * Calculates the individual price of a food item based on its quantity.
     *
     * @param item The food item for which to calculate the individual price.
     * @return The calculated individual price.
     */
    private fun calculateIndividualPrice(item: FoodItem): Double {
        val price = item.price.removePrefix("$").toDouble()
        return item.quantity * price
    }

    /**
     * Nested [FoodItemsAdapter] class for managing the RecyclerView's food item views.
     */
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

        /**
         * Sets the list of food items in the adapter and triggers a data set change.
         *
         * @param items The list of food items to set in the adapter.
         */
        fun setFoodItems(items: List<FoodItem>) {
            foodItems = items
            notifyDataSetChanged()
        }

        /**
         * Gets the list of selected food items with a quantity greater than zero.
         *
         * @return The list of selected food items.
         */
        fun getSelectedItems(): List<FoodItem> {
            return foodItems.filter { it.quantity > 0 }
        }

        /**
         * [FoodItemViewHolder] inner class for holding the individual food item views.
         */
        inner class FoodItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            /**
             * Binds the data of a food item to the view.
             *
             * @param foodItem The food item to bind.
             */
            fun bind(foodItem: FoodItem) {
                itemView.findViewById<TextView>(R.id.foodItemName).text = foodItem.name
                itemView.findViewById<TextView>(R.id.foodItemPrice).text = foodItem.price
                itemView.findViewById<TextView>(R.id.quantityText).text = foodItem.quantity.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
