package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myhabits3.viewModels.MainViewModel
import com.example.myhabits3.R
import com.example.myhabits3.adapters.MainAdapter
import com.example.myhabits3.model.Habit
import com.example.myhabits3.utils.SpacingItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bad_habits.*

class FragmentBadHabits : FragmentHabits() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.currentHabits.observe(viewLifecycleOwner, { habits ->
            habits?.let { list ->
                adapter.setData(list.filter {
                    !(it.type.toBoolean())
                }.toMutableList())
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): FragmentBadHabits = FragmentBadHabits()
    }
}