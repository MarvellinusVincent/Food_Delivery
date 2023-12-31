package com.example.fooddeliveryapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddeliveryapp.databinding.FragmentCheckoutBinding

/**
 * Fragment for handling the checkout process, including displaying selected items and placing an order.
 */
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        val view = binding.root

        /** Retrieve selected items from arguments */
        val selectedItems = arguments?.getParcelableArrayList<FoodItem>("selectedItems")
        val recyclerView = binding.checkoutRecyclerView
        val checkoutAdapter = CheckoutAdapter()
        recyclerView.adapter = checkoutAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        /** Pass selected items to the adapter */
        checkoutAdapter.setSelectedItems(selectedItems)

        binding.checkoutModifyOrderButton.setOnClickListener {
            val selectedItems = checkoutAdapter.getSelectedItems()
            val bundle = Bundle().apply {
                putParcelableArrayList("selectedItems", ArrayList(selectedItems))
            }
            view.findNavController().navigate(R.id.action_checkoutFragment_to_restaurantFragment, bundle)
        }

        binding.checkoutPlaceOrderButton.setOnClickListener {
            val selectedItems = checkoutAdapter.getSelectedItems()
            val deliveryAddress = binding.checkoutDeliveryAddressEditText.text.toString().trim()

            if (selectedItems.isNotEmpty() && deliveryAddress.isNotEmpty()) {
                /** Save order details to the order database */
                saveOrderDetails(selectedItems, deliveryAddress)

                /** Navigate to the Orders screen */
                view?.findNavController()?.navigate(R.id.action_checkoutFragment_to_ordersFragment)
            } else {
                Log.d("Checkout Fragment", "address emoty")
            }
        }
        return view
    }

    /**
     * Saves order details to the order database using the ViewModel.
     */
    private fun saveOrderDetails(selectedItems: List<FoodItem>, deliveryAddress: String) {
        viewModel.saveOrder(selectedItems, deliveryAddress)
    }

    inner class CheckoutAdapter : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

        private var selectedItems: List<FoodItem> = emptyList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.checkout_item, parent, false)
            return CheckoutViewHolder(view)
        }

        override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
            val selectedItem = selectedItems[position]
            holder.bind(selectedItem)
            holder.itemView.findViewById<ImageButton>(R.id.addCheckoutButton).setOnClickListener {
                selectedItem.quantity++
                val priceWithoutDollarSign = selectedItem.price.replace("$", "")
                selectedItem.individualPrice += priceWithoutDollarSign.toDouble()
                notifyItemChanged(position)
            }
            holder.itemView.findViewById<ImageButton>(R.id.deleteCheckoutButton).setOnClickListener {
                if (selectedItem.quantity > 0) {
                    selectedItem.quantity--
                    val priceWithoutDollarSign = selectedItem.price.replace("$", "")
                    selectedItem.individualPrice = priceWithoutDollarSign.toDouble()
                    notifyItemChanged(position)
                }
            }
        }

        override fun getItemCount(): Int = selectedItems.size

        fun setSelectedItems(items: List<FoodItem>?) {
            selectedItems = items ?: emptyList()
            notifyDataSetChanged()
        }

        fun getSelectedItems(): List<FoodItem> {
            return selectedItems
        }

        inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            /**
             * Binds the data of a selected item to the view.
             */
            fun bind(selectedItem: FoodItem) {
                itemView.findViewById<TextView>(R.id.checkoutItemName).text = selectedItem.name
                itemView.findViewById<TextView>(R.id.checkoutItemQuantity).text =
                    "Quantity: ${selectedItem.quantity}"
                itemView.findViewById<TextView>(R.id.checkoutItemPrice).text =
                    "$${selectedItem.individualPrice}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
