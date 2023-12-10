package com.example.fooddeliveryapp

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class DeliveryViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var restaurantCollection: DatabaseReference
    private lateinit var orderCollection: DatabaseReference
    private lateinit var context: Context
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var lastKnownLocation: Location? = null


    var user: User = User()
    var name = ""
    var restaurantName: String = ""
    var restaurant = MutableLiveData<Restaurant>()

    private val _restaurants: MutableLiveData<MutableList<Restaurant>> = MutableLiveData()
    private val _orders: MutableLiveData<MutableList<Order>> = MutableLiveData()
    private val _navigateToHome = MutableLiveData<Boolean>(false)
    private val _navigateToRestaurant = MutableLiveData<String?>()
    private val _navigateToSignUp = MutableLiveData<Boolean>(false)
    private val _navigateToSignIn = MutableLiveData<Boolean>(false)
    private val _errorHappened = MutableLiveData<String?>()
    private val _filteredOrders: MutableLiveData<MutableList<Order>> = MutableLiveData()
    private var ordersList: List<Order> = emptyList()

    val restaurants: LiveData<List<Restaurant>>
        get() = _restaurants as LiveData<List<Restaurant>>
    val orders: LiveData<List<Order>>
        get() = _orders as LiveData<List<Order>>
    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome
    val navigateToRestaurant: LiveData<String?>
        get() = _navigateToRestaurant
    val navigateToSignUp: LiveData<Boolean>
        get() = _navigateToSignUp
    val navigateToSignIn: LiveData<Boolean>
        get() = _navigateToSignIn
    val errorHappened: LiveData<String?>
        get() = _errorHappened
    val filteredOrders: LiveData<List<Order>>
        get() = _filteredOrders as LiveData<List<Order>>

    init {
        Log.d(TAG, "viewmodel initialized")
        auth = Firebase.auth
        context = FirebaseApp.getInstance().applicationContext
        initializeTheRestaurantDatabaseReference()
        if (_restaurants.value?.isEmpty() == true) {
            initializeDummyRestaurantsData()
        }
        if (_orders.value?.isEmpty() == true) {
            initializeDummyOrdersData()
        }

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Update lastKnownLocation when the location changes
                lastKnownLocation = location
                Log.d("UserLocation", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Handle status changes if needed
            }

            override fun onProviderEnabled(provider: String) {
                // Handle provider enable if needed
            }

            override fun onProviderDisabled(provider: String) {
                // Handle provider disable if needed
            }
        }

        // Request location updates
        requestLocationUpdates()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun initializeTheRestaurantDatabaseReference() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val database = Firebase.database
            restaurantCollection = database
                .getReference("restaurant")
                .child(auth.currentUser!!.uid)
            restaurantCollection.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var restaurantList: ArrayList<Restaurant> = ArrayList()
                    for (restaurantSnapshot in dataSnapshot.children) {
                        var restaurant = restaurantSnapshot.getValue<Restaurant>()
                        restaurant?.restaurantName = restaurantSnapshot.key!!
                        restaurantList.add(restaurant!!)
                    }
                    _restaurants.value = restaurantList
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
            orderCollection = database
                .getReference("previous_orders")
                .child(auth.currentUser!!.uid)
            orderCollection.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var orderList: ArrayList<Order> = ArrayList()
                    for (orderSnapshot in dataSnapshot.children) {
                        var order = orderSnapshot.getValue<Order>()
                        order?.restaurantName = orderSnapshot.key!!
                        orderList.add(order!!)
                    }
                    _orders.value = orderList
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
    }

    fun initializeDummyRestaurantsData() {
        val userId = auth.currentUser?.uid ?: return
        val database = Firebase.database
        restaurantCollection = database.getReference("restaurant").child(userId)
        restaurantCollection.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentItemCount = dataSnapshot.childrenCount.toInt()
                if (currentItemCount < 9) {
                    val dummyRestaurants = mutableListOf<Restaurant>()
                    for (i in (currentItemCount + 1)..9) {
                        val dummyRestaurant =  createDummyRestaurant(i)
                        dummyRestaurants.add(dummyRestaurant)
                        restaurantCollection.child(dummyRestaurant.restaurantName).setValue(dummyRestaurant)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }

    fun initializeDummyOrdersData() {
        val userId = auth.currentUser?.uid ?: return
        val database = Firebase.database
        orderCollection = database.getReference("previous_orders").child(userId)
        orderCollection.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentItemCount = dataSnapshot.childrenCount.toInt()
                if (currentItemCount < 6) {
                    val dummyOrders = mutableListOf<Order>()
                    val usedNumbers = mutableSetOf<Int>()

                    while (dummyOrders.size < 6 - currentItemCount) {
                        val randomValue = generateRandomNumber(usedNumbers)
                        usedNumbers.add(randomValue)

                        val dummyOrder = Order(
                            restaurantName = "Restaurant $randomValue",
                            foodName = "",
                            quantity = "",
                            price = "",
                            date = null,
                            time = null,
                            deliveryAddress = ""
                        )

                        dummyOrders.add(dummyOrder)
                        orderCollection.child(dummyOrder.restaurantName).setValue(dummyOrder)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun createDummyRestaurant(index: Int): Restaurant {
        val dummyRestaurant = Restaurant(
            restaurantName = "Restaurant $index",
            firstImage = R.drawable.restaurant1,
            secondImage = R.drawable.restaurant2,
            thirdImage = R.drawable.restaurant3
        )

        // Generate a random location within 50 miles of the user
        val userLocation = getUserLocation()
        if (userLocation != null) {
            val randomLocation = getRandomLocation(userLocation)
            dummyRestaurant.location = SimpleLocation(randomLocation.latitude, randomLocation.longitude)
        }

        return dummyRestaurant
    }

    private fun getUserLocation(): Location? {
        return lastKnownLocation
    }

    private fun getRandomLocation(userLocation: Location): Location {
        val geocoder = Geocoder(context)
        val maxDistanceMiles = 50.0
        val randomRadius = Random.nextDouble(0.0, maxDistanceMiles)
        val randomAngle = Random.nextDouble(0.0, 360.0)
        val randomLocation = Location("Random Location")

        // Convert miles to meters
        val distanceMeters = randomRadius * 1609.34

        // Calculate new location based on user's location, random distance, and angle

        val lat = userLocation.latitude + (distanceMeters / 111111.0) * Math.cos(Math.toRadians(randomAngle))
        val lon = userLocation.longitude + (distanceMeters / 111111.0) * Math.sin(Math.toRadians(randomAngle))

        randomLocation.latitude = lat
        randomLocation.longitude = lon

        return randomLocation
    }

    private fun requestLocationUpdates() {
        // Use criteria to choose the best provider based on your requirements
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, false)

        // Check for permissions before requesting location updates
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 1000, 0.1f, locationListener)
            }

            // Get the last known location
            lastKnownLocation = provider?.let { locationManager.getLastKnownLocation(it) }
        } else {
            Log.e(TAG, "Location permissions not granted.")
        }
    }


    fun signUp() {
        if (user.email.isEmpty() || user.name.isEmpty()) {
            _errorHappened.value = "Name and email cannot be empty."
            return
        }

        // Provide a dummy password (can be empty or any string since it won't be used for authentication)
        val dummyPassword = "dummyPassword"

        auth.createUserWithEmailAndPassword(user.email, dummyPassword)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val firebaseUser = authResult.result?.user

                    // Set the display name for the user
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(user.name)
                        .build()

                    firebaseUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateProfileTask ->
                            if (updateProfileTask.isSuccessful) {
                                // Display name updated successfully
                                if (!user.profilePictureUri.isNullOrEmpty()) {
                                    // Set profile picture URI if it's not null or empty
                                    val photoUri = Uri.parse(user.profilePictureUri)
                                    val photoUpdate = UserProfileChangeRequest.Builder()
                                        .setPhotoUri(photoUri)
                                        .build()

                                    firebaseUser.updateProfile(photoUpdate)
                                        .addOnCompleteListener { updatePhotoTask ->
                                            if (updatePhotoTask.isSuccessful) {
                                                // Profile picture URI updated successfully
                                                initializeDummyRestaurantsData()
                                                initializeDummyOrdersData()
                                                _navigateToHome.value = true
                                            } else {
                                                _errorHappened.value = updatePhotoTask.exception?.message
                                            }
                                        }
                                } else {
                                    // No profile picture URI to set
                                    initializeDummyRestaurantsData()
                                    initializeDummyOrdersData()
                                    _navigateToHome.value = true
                                }
                            } else {
                                _errorHappened.value = updateProfileTask.exception?.message
                            }
                        }
                } else {
                    _errorHappened.value = authResult.exception?.message
                }
            }
    }

    fun signIn() {
        if (user.email.isEmpty()) {
            _errorHappened.value = "Email cannot be empty."
            return
        }
        auth.signInWithCustomToken(user.email).addOnCompleteListener {
            if (it.isSuccessful) {
                initializeDummyRestaurantsData()
                initializeDummyOrdersData()
                _navigateToHome.value = true
            } else {
                _errorHappened.value = it.exception?.message
            }
        }
    }

    fun signOut() {
        Log.d(TAG, "sign out button clicked")
        auth.signOut()
        _restaurants.value = mutableListOf()
        _orders.value = mutableListOf()
        _navigateToSignUp.value = true
    }

    fun generateRandomNumber(usedNumbers: Set<Int>): Int {
        var randomValue: Int
        do {
            randomValue = Random.nextInt(1, 7)
        } while (randomValue in usedNumbers)
        return randomValue
    }

    fun onRestaurantClicked(selectedRestaurant: Restaurant) {
        _navigateToRestaurant.value = selectedRestaurant.restaurantName
        restaurantName = selectedRestaurant.restaurantName
        restaurant.value = selectedRestaurant
    }

    fun saveOrder(selectedItems: List<FoodItem>, deliveryAddress: String) {
        val dateFormat = SimpleDateFormat("HH:mm:ss")
        val currentTime = Time(System.currentTimeMillis())
        val formattedTime = dateFormat.format(currentTime)
        val order = Order(
            restaurantName = selectedItems.firstOrNull()?.restaurantName ?: "",
            foodName = selectedItems.joinToString { it.name },
            price = selectedItems.sumOf { it.individualPrice }.toString(),
            quantity = selectedItems.sumOf { it.quantity }.toString(),
            date = Date(),
            time = formattedTime,
            deliveryAddress = deliveryAddress
        )

        // Save order to the order database
        orderCollection.child(order.restaurantName).setValue(order)
    }

    fun calculateDeliveryTime(): Long {
        // Assuming you have the location information and lastKnownLocation available
        val distanceBetween = calculateDistanceBetween()
        val randomFactor = Random.nextDouble(5.0, 100.0)
        return ((distanceBetween / 100) * randomFactor).toLong()
    }

    private fun calculateDistanceBetween(): Double {
        // Implement the logic to calculate the distance between the Restaurant and Delivery address
        // You can use the Haversine formula or other methods
        // Example: (This is a dummy implementation, replace it with the actual logic)
        return if (lastKnownLocation != null && restaurant.value != null) {
            lastKnownLocation!!.distanceTo(getRestaurantLocation()).toDouble() // Distance in meters
        } else {
            0.0
        }
    }

    private fun getRestaurantLocation(): Location {
        val restaurantLocation = Location("Restaurant Location")
        restaurantLocation.latitude = restaurant.value?.location?.latitude ?: 0.0
        restaurantLocation.longitude = restaurant.value?.location?.longitude ?: 0.0
        return restaurantLocation
    }

    fun getOrders(currentRestaurant: String): List<Order> {
        val orders = _orders.value ?: emptyList()
        orders.filter { order ->
            val isMatchingRestaurant = order.restaurantName == currentRestaurant
            if (isMatchingRestaurant) {
                // Log the order if the restaurantName matches
                Log.d(TAG, "Matching Order: $order")
            }
            isMatchingRestaurant
        }
        return _orders.value?.filter { it.restaurantName == currentRestaurant } ?: emptyList()
    }

    fun getAllOrders(): List<Order> {
        val orders = _orders.value ?: emptyList()
        orders.filter { order ->
            val isNotMatchingQuantity = order.quantity != ""
            if (isNotMatchingQuantity) {
                Log.d(TAG, "Matching Order: $order")
            }
            isNotMatchingQuantity
        }
        return _orders.value?.filter { it.quantity != ""} ?: emptyList()
    }

    fun getAllOrderDates(): List<Date> {
        Log.d("Viewmodel", "${_orders.value}")
        val orderDates = _orders.value?.map { it.date } ?: emptyList()
        return orderDates.filterNotNull()
    }

    fun getOrdersForDate(selectedDate: Date): List<Order> {
        return _orders.value?.filter { isSameDate(it.date, selectedDate) } ?: emptyList()
    }

    private fun isSameDate(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) {
            return false
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(date1) == dateFormat.format(date2)
    }

    fun getProfilePictureUri(userId: String): String? {
        // Replace this with your actual code to fetch the profile picture URI from the database
        // For example, if you're using Firebase Realtime Database:
        val databaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/profilePictureUri")

        // Read the profile picture URI from the database
        var profilePictureUri: String? = null
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profilePictureUri = snapshot.getValue(String::class.java)
                // Update the UI or do any further processing with the profilePictureUri
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })

        return profilePictureUri
    }

    fun navigateToSignUp() {
        _navigateToSignUp.value = true
    }

    fun navigateToSignIn() {
        _navigateToSignIn.value = true
    }

    fun onNavigatedToSignUp() {
        _navigateToSignUp.value = false
    }

    fun onNavigatedToSignIn() {
        _navigateToSignIn.value = false
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }

    fun onNavigatedToRestaurant() {
        _navigateToRestaurant.value = null
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.removeUpdates(locationListener)
    }

}