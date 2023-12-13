package com.example.fooddeliveryapp

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.fooddeliveryapp.databinding.FragmentSignUpBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import com.bumptech.glide.Glide

/**
 * Fragment for user sign-up and registration.
 */
class SignUpFragment : Fragment() {
    /** Tag for logging purposes. */
    val TAG = "SignUpFragment"
    private var uri: Uri? = null

    /** Binding for the fragment. */
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    val pickMediaUpload = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d(TAG, "Image location : $uri")
            Glide.with(this.requireContext()).load(uri).into(binding.profilePictureAssign)
            binding.viewModel?.user?.profilePictureUri = uri.toString()
        } else {
            Log.e(TAG, "Image not saved.")
        }
    }

    val pickMediaTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { navigate ->
        if (navigate) {
            Log.d(TAG, "Image location : $uri")
            Glide.with(this.requireContext()).load(uri).into(binding.profilePictureAssign)
            binding.viewModel?.user?.profilePictureUri = uri.toString()
        } else {
            Log.e(TAG, "Image not saved.")
        }
    }

    /**
     * Inflates the fragment's layout and sets up the UI components.
     *
     * @param inflater The LayoutInflater object to inflate views.
     * @param container The parent view to attach the fragment's UI.
     * @param savedInstanceState The saved state of the fragment.
     * @return The root view of the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).setToolbarVisibility(false)
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        /** Get the shared ViewModel for handling user registration. */
        val viewModel: DeliveryViewModel by activityViewModels()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val photoFile = createImageFile()
        try {
            uri = FileProvider.getUriForFile(this.requireContext(), "com.example.file-provider", photoFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
        }

        binding.profilePictureAssign.setOnClickListener {
            Log.i(TAG, "Open up the camera application on device")
            showPictureSourceDialog()
        }
        /** Observe to navigate to the sign in fragment. */
        viewModel.navigateToSignIn.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                view.findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                viewModel.onNavigatedToSignIn()
            }
        })

        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                view.findNavController().navigate(R.id.action_signUpFragment_to_HomeFragment)
                viewModel.onNavigatedToHome()
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

    fun createImageFile() : File {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(Date())
        val imageDirectory = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("file_${timestamp}"
            , ".jpg"
            , imageDirectory)
    }

    private fun showPictureSourceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.image_upload_dialog_box, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        dialogTitle.text = "Choose Picture Source"
        val dialogText = dialogView.findViewById<TextView>(R.id.dialogText)
        dialogText.text = "Enable access so you can take photos"

        val buttonTakePicture = dialogView.findViewById<Button>(R.id.buttonTakePicture)
        val buttonUploadGallery = dialogView.findViewById<Button>(R.id.buttonUploadGallery)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonTakePicture.setOnClickListener {
            takePicture()
            alertDialog.dismiss()
        }

        buttonUploadGallery.setOnClickListener {
            pickFromGallery()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun takePicture() {
        uri?.let {
            pickMediaTakePicture.launch(uri)
        } ?: run {
            Log.e(TAG, "URI is null.")
        }

    }

    private fun pickFromGallery() {
        uri?.let {
            /** Use the pickMedia launcher to select an image from the gallery */
            pickMediaUpload.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } ?: run {
            Log.e(TAG, "URI is null.")
        }
    }

    /**
     * Cleans up the binding when the fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
