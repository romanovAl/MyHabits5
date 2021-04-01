package com.example.myhabits3.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myhabits3.ui.fragments.FragmentBadHabits
import com.example.myhabits3.ui.fragments.FragmentGoodHabits

class ViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> FragmentGoodHabits.newInstance()
        else -> FragmentBadHabits.newInstance()
    }
}