package com.example.fooddeliveryapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.PagerAdapter
import com.example.fooddeliveryapp.databinding.FragmentImagesRestaurantBinding
import com.example.fooddeliveryapp.databinding.RestaurantImageViewPagerBinding

class ImagesRestaurantFragment : Fragment() {

    private var _binding: FragmentImagesRestaurantBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagesRestaurantBinding.inflate(inflater, container, false)
        val view = binding.root

        // Get the restaurant object from the ViewModel
        val restaurant = viewModel.restaurant.value

        // Check if the restaurant object is not null
        if (restaurant != null) {
            // Create an adapter for the ViewPager
            val adapter = ImagesPagerAdapter(restaurant)
            binding.viewPager.adapter = adapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ImagesPagerAdapter inner class
    inner class ImagesPagerAdapter(private val restaurant: Restaurant) : PagerAdapter() {

        override fun getCount(): Int {
            // Return the number of images (assuming there are three images for each restaurant)
            return 3
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val binding =
                RestaurantImageViewPagerBinding.inflate(LayoutInflater.from(container.context), container, false)
            val imageView = binding.pagerImageView

            // Load the image into the ImageView based on the position
            when (position) {
                0 -> imageView.setImageResource(restaurant.firstImage)
                1 -> imageView.setImageResource(restaurant.secondImage)
                2 -> imageView.setImageResource(restaurant.thirdImage)
            }

            container.addView(binding.root)
            return binding.root
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
