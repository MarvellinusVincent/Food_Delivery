package com.example.fooddeliveryapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser

val TAG = "Main Activity"
val REQUEST_CODE = 123
class MainActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var navView: NavigationView
    private val viewModel: DeliveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        // Identify top-level destinations
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.splashFragment,
            R.id.signInFragment,
            R.id.signUpFragment,
            R.id.restaurantFragment,
            R.id.checkoutFragment,
            R.id.ordersFragment,
            R.id.recentOrderFragment,
            R.id.calendarFragment
            // Add other top-level destinations as needed
        )

        val builder = AppBarConfiguration.Builder(topLevelDestinations)
        builder.setOpenableLayout(drawer)

        val appBarConfiguration = builder.build()
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navView = findViewById(R.id.nav_view)
        NavigationUI.setupWithNavController(navView, navController)
        setupMenuClickListener(navView)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission granted")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
        }
        updateUserInfo()
    }

    /*
      Add any menu items to the toolbar
    */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    /*
       Navigate to a destination when an item is clicked.
    */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    fun setToolbarVisibility(isVisible: Boolean) {
        supportActionBar?.let {
            if (isVisible) {
                it.show()
            } else {
                it.hide()
            }
        }
    }

    private fun setupMenuClickListener(navView: NavigationView) {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signOut -> {
                    Log.d(TAG, "sign out button clicked")
                    // Call the signOut function in the ViewModel
                    viewModel.signOut()
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.action_global_signUpFragment)
                    val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                    drawer.closeDrawers()
                    updateUserInfo()
                    true
                }
                R.id.recentOrders -> {
                    // Navigate to the Recent Orders fragment
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.recentOrderFragment)
                    val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                    drawer.closeDrawers()
                    true
                }
                R.id.calendarView -> {
                    // Navigate to the Recent Orders fragment
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.calendarFragment)
                    val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                    drawer.closeDrawers()
                    true
                }
                // Add other menu item click handling as needed
                else -> false
            }
        }
    }

    private fun setUserInfoInDrawerHeader(user: FirebaseUser?) {
        val navHeader = navView.getHeaderView(0)
        val nameTextView = navHeader.findViewById<TextView>(R.id.nameDrawerHeader)
        val emailTextView = navHeader.findViewById<TextView>(R.id.emailDrawerHeader)
        val profilePicture = navHeader.findViewById<ImageView>(R.id.profilePictureSet)

        if (user != null) {
            Log.d(TAG, "user logged in")
            // Set the user's name and email in the navigation drawer header
            val profilePictureUri = viewModel.getProfilePictureUri(user.uid)
            Glide.with(this)
                .load(profilePictureUri)
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .circleCrop()
                .into(profilePicture)
            nameTextView.text = user.displayName
            emailTextView.text = user.email
        } else {
            Log.d(TAG, "user not logged in")
            // If user is not logged in, set default values or hide the views
            nameTextView.text = "Name"
            emailTextView.text = "Email"
        }
    }

    private fun updateUserInfo() {
        val currentUser = viewModel.getCurrentUser()
        setUserInfoInDrawerHeader(currentUser)
    }
}