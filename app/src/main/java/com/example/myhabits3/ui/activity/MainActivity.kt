package com.example.myhabits3.ui.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.myhabits3.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_main_fragment.*
import kotlinx.android.synthetic.main.header_layout.view.*

class MainActivity : DaggerAppCompatActivity() {

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

        val navDrawer = navigationView.getHeaderView(0)
        val imgView: ImageView = navDrawer.fragmentAboutImageView

        Glide.with(this)
            .load(LOGO_URI)
            .override(150, 150)
            .placeholder(ColorDrawable(Color.BLACK))
            .error(R.drawable.ic_launcher_background)
            .transform(CircleCrop())
            .into(imgView)
    }

    override fun onBackPressed() {
        if (navigationDrawerLayout != null && bottomSheetMainFragment != null) {

            val behavior = BottomSheetBehavior.from(bottomSheetMainFragment)

            when {
                navigationDrawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    navigationDrawerLayout.closeDrawer(GravityCompat.START)
                }
                behavior.state != BottomSheetBehavior.STATE_COLLAPSED -> {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else -> {
                    super.onBackPressed()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val LOGO_URI = "https://doubletapp.ru/wp-content/uploads/2018/12/logo_for_vk.png"
    }
}