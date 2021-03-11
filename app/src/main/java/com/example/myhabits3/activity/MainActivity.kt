package com.example.myhabits3.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.myhabits3.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navController: NavController by lazy {
        findNavController(R.id.fragmentMain)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbarMainActivity)

        val appBarConfiguration = AppBarConfiguration(navController.graph, navigationDrawerLayout)

        toolbarMainActivity.setupWithNavController(navController, appBarConfiguration)

        navigationView.setupWithNavController(navController)

        if (savedInstanceState == null) {
            navController.setGraph(R.navigation.navigation_graph)
        }
    }

    override fun onBackPressed() {
        if (navigationDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            navigationDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}