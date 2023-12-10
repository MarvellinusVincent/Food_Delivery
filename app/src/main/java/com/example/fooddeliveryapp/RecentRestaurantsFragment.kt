package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
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
import com.example.fooddeliveryapp.databinding.RestaurantItemBinding

class RecentRestaurantsFragment : Fragment() {

    private var _binding: FragmentRecentRestaurantsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecentRestaurantsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up RecyclerView
        val recyclerView = binding.recyclerViewRecentRestaurants
        val adapter = RecentRestaurantAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Observe the list of restaurants in the ViewModel
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

    inner class RecentRestaurantAdapter : androidx.recyclerview.widget.ListAdapter<Restaurant, RecentRestaurantAdapter.RecentRestaurantViewHolder>(AllRestaurantDiffItemCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentRestaurantViewHolder {
            val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RecentRestaurantViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecentRestaurantViewHolder, position: Int) {
            val order = getItem(position)
            holder.bind(order)
        }

        inner class RecentRestaurantViewHolder(private val binding: OrderItemBinding) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

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