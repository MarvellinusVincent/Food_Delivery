package com.example.fooddeliveryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.databinding.FragmentRecentRestaurantsBinding
import com.example.fooddeliveryapp.databinding.OrderItemBinding

/**
 * Fragment responsible for displaying a list of recent restaurants.
 */
class RecentRestaurantsFragment : Fragment() {

    private var _binding: FragmentRecentRestaurantsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecentRestaurantsBinding.inflate(inflater, container, false)
        val view = binding.root

        /** Set up RecyclerView */
        val recyclerView = binding.recyclerViewRecentRestaurants
        val adapter = RecentRestaurantAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        /** Observe the list of restaurants in the ViewModel */
        viewModel.restaurants.observe(viewLifecycleOwner, Observer { restaurants ->
            adapter.submitList(restaurants)
        })

        viewModel.navigateToRestaurant.observe(viewLifecycleOwner, Observer { restaurantName ->
            restaurantName?.let {
                viewModel.restaurantName = restaurantName
                val action = HomeFragmentDirections
                    .actionHomeFragmentToRestaurantFragment(restaurantName)
                this.findNavController().navigate(action)
                viewModel.onNavigatedToRestaurant()
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initializeDummyRestaurantsData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * RecyclerView Adapter for displaying recent restaurants.
     */
    inner class RecentRestaurantAdapter : androidx.recyclerview.widget.ListAdapter<Restaurant, RecentRestaurantAdapter.RecentRestaurantViewHolder>(AllRestaurantDiffItemCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentRestaurantViewHolder {
            val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecentRestaurantViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecentRestaurantViewHolder, position: Int) {
            val restaurant = getItem(position)
            holder.bind(restaurant)
        }

        /**
         * ViewHolder for the recent restaurant items.
         */
        inner class RecentRestaurantViewHolder(private val binding: OrderItemBinding) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

            /**
             * Binds the restaurant data to the item view and sets up click listener.
             *
             * @param restaurant The [Restaurant] object to be displayed.
             */
            fun bind(restaurant: Restaurant) {
                binding.restaurant = restaurant
                binding.executePendingBindings()
                binding.root.setOnClickListener {
                    viewModel.onRestaurantClicked(restaurant)
                }
            }
        }
    }
}
