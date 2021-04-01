package com.example.myhabits3.ui.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_good_habits.*

class FragmentGoodHabits : FragmentHabits() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.currentHabits.observe(viewLifecycleOwner, { habits ->
            habits?.let { list ->
                adapter.setData(list.filter {
                    it.type.toBoolean()
                }.toMutableList())
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): FragmentGoodHabits = FragmentGoodHabits()
    }

}