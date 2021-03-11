package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.myhabits3.R
import com.example.myhabits3.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    private val navController: NavController by lazy {
        Navigation.findNavController(requireView())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager2.adapter = ViewPagerAdapter(this)
        val tabNames =
            listOf(getString(R.string.view_pager_good), getString(R.string.view_pager_bad))

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

        fabAddHabit.setOnClickListener {
            val action =
                MainFragmentDirections.actionFragmentMainToFragmentAddEdit(getString(R.string.label_add))
            navController.navigate(action)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}