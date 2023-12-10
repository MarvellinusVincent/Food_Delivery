package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fooddeliveryapp.databinding.FragmentAllRestaurantsBinding
import com.example.fooddeliveryapp.databinding.RestaurantItemBinding

class AllRestaurantsFragment : Fragment() {

    private var _binding: FragmentAllRestaurantsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAllRestaurantsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up RecyclerView
        val recyclerView = binding.recyclerViewAllRestaurants
        val adapter = RestaurantAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

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
                Log.d("AllRestaurantsFragment", "Navigate to restaurant: $restaurantName")
                viewModel.onNavigatedToRestaurant()
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AllRestaurantsFragment", "Called")
        viewModel.initializeDummyRestaurantsData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // RestaurantAdapter inner class
    inner class RestaurantAdapter : androidx.recyclerview.widget.ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(AllRestaurantDiffItemCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
            val binding = RestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return RestaurantViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
            val restaurant = getItem(position)
            holder.bind(restaurant)
        }

        inner class RestaurantViewHolder(private val binding: RestaurantItemBinding) :
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
