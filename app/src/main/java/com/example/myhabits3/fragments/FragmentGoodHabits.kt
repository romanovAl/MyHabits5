package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerGoodHabits.adapter = adapter
        recyclerGoodHabits.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )

        viewModel.currentHabits.observe(viewLifecycleOwner, { habits ->
            habits?.let { list ->
                adapter.setData(list.filter { it.type.toBoolean() } as MutableList<Habit>)
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun Int.toBoolean(): Boolean = this == 1

    companion object {
        const val DISTANCE_BETWEEN_ELEMENTS = 50

        fun newInstance(): FragmentGoodHabits = FragmentGoodHabits()
    }

}