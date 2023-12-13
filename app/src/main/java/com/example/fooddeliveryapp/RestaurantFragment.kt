package com.example.fooddeliveryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.fooddeliveryapp.databinding.FragmentRestaurantBinding

/**
 * Fragment responsible for displaying details about a specific restaurant.
 */
class RestaurantFragment : Fragment() {
    val viewModel : DeliveryViewModel by activityViewModels()

    private var _binding: FragmentRestaurantBinding? = null
    private val binding get() = _binding!!

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).setToolbarVisibility(true)
        _binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Called when the view is being destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}