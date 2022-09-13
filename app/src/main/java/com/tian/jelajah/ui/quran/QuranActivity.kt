package com.tian.jelajah.ui.quran

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tian.jelajah.R
import com.tian.jelajah.databinding.ActivityQuranBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuranActivity : AppCompatActivity() {
    private val binding : ActivityQuranBinding by viewBinding()

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val name = destination.label.toString()
            val resource = getStringID(name)
            Log.e("TAG", "onCreate: ${destination.label}  | ${destination.id} | $resource")
        }



        val carArr = getResources().getStringArray(R.array.car_array)
        carArr.forEachIndexed { index, value ->
            Log.e("TAG", "onCreateasd: $index $value ")
        }

//        val menu: Menu = binding.navView.getMenu()
//        val items = arrayListOf<Int>()
//        for (i in 0 until menu.size()) {
//            items.add(Menu.NONE, Menu.NONE, i, menu.getItem(i))
//        }
//        binding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem -> // update highlighted item in the navigation menu
//            menuItem.isChecked = true
//            val position: Int = items.getOrder()
//            true
//        })

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        val menu = binding.navView.getMenu()

        val items: ArrayList<MenuItem> = ArrayList()
        for (i in 0 until menu.size()) {
            items.add(menu.getItem(i))
        }
        binding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
              // update highlighted item in the navigation menu

            Log.e("TAG", "onCreateorder: ${menuItem.order} ${items.indexOf(menuItem)}")
            when (menuItem.order) {
                0 -> {}
                else -> {}
            }
            true
        })
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

    }

    fun AppCompatActivity.getStringID(name: String): Int {
        return resources.getIdentifier(name, "string", packageName)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}