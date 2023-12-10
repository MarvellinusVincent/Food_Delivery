package com.example.fooddeliveryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.fooddeliveryapp.databinding.FragmentHomeBinding
import com.example.fooddeliveryapp.databinding.FragmentRestaurantBinding

class RestaurantFragment : Fragment() {
    val viewModel : DeliveryViewModel by activityViewModels()

    private var _binding: FragmentRestaurantBinding? = null
    private val binding get() = _binding!!

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