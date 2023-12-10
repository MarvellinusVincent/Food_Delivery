package com.example.fooddeliveryapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 * Fragment for handling the application's splash screen and user authentication check.
 */
class SplashFragment : Fragment() {
    /** ViewModel for managing user restaurants and authentication. */
    val viewModel: DeliveryViewModel by activityViewModels()

    /**
     * Inflates the fragment's layout.
     *
     * @param inflater The LayoutInflater object to inflate views.
     * @param container The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).setToolbarVisibility(false)
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        return view
    }

    /**
     * Called after the view has been created. Currently does nothing.
     *
     * @param view The view that has been created.
     * @param savedInstanceState The saved state of the fragment.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Called when the fragment is visible to the user.
     */
    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.getCurrentUser()
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            if (currentUser != null) {
                this.findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                this.findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
