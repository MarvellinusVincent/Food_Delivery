package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.fooddeliveryapp.databinding.FragmentHomeBinding

val TAG2 = "Home Fragment"
class HomeFragment : Fragment() {
    val viewModel : DeliveryViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).setToolbarVisibility(true)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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