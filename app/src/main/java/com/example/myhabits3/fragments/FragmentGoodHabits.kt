package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myhabits3.viewModels.MainViewModel
import com.example.myhabits3.R
import com.example.myhabits3.adapters.MainAdapter
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.SpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_good_habits.*

class FragmentGoodHabits : Fragment(R.layout.fragment_good_habits) {

    private val adapter: MainAdapter by lazy {
        val data = ArrayList<Habit>()
        MainAdapter(data, requireContext())
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, defaultViewModelProviderFactory).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerGoodHabits.adapter = adapter
        recyclerGoodHabits.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )

        viewModel.sortedHabits.observe(viewLifecycleOwner, { sortedHabits ->

            viewModel.habits.observe(viewLifecycleOwner, { habits ->
                if (sortedHabits.isNotEmpty()) {
                    adapter.setData(sortedHabits.filter { it.type.toBoolean() } as MutableList)
                } else {
                    adapter.setData(habits.filter { it.type.toBoolean() } as MutableList)
                }
            })

        })


        super.onViewCreated(view, savedInstanceState)
    }

    private fun Int.toBoolean(): Boolean = this == 1

    companion object {
        const val DISTANCE_BETWEEN_ELEMENTS = 50

        fun newInstance(): FragmentGoodHabits = FragmentGoodHabits()
    }

}