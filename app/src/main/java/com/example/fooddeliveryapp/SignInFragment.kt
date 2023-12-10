package com.example.fooddeliveryapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.fooddeliveryapp.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    /** Tag for logging purposes. */
    val TAG = "SignInFragment"

    /** Binding for the fragment. */
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the fragment's layout and sets up the UI components.
     *
     * @param inflater The LayoutInflater object to inflate views.
     * @param container The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        (activity as MainActivity).setToolbarVisibility(false)
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root

        /** Get the shared ViewModel for handling user authentication. */
        val viewModel: DeliveryViewModel by activityViewModels()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                view.findNavController().navigate(R.id.action_signUpFragment_to_HomeFragment)
                viewModel.onNavigatedToHome()
            }
        })

        /** Observe to navigate to the sign up fragment. */
        viewModel.navigateToSignUp.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                view.findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
                viewModel.onNavigatedToSignUp()
            }
        })

        /** Observe to show error toast. */
        viewModel.errorHappened.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}