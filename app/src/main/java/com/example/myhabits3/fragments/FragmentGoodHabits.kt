package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.myhabits3.R
import com.example.myhabits3.adapters.MainAdapter
import com.example.myhabits3.data.FakeDatabase
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.SpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_good_habits.*

class FragmentGoodHabits : Fragment(R.layout.fragment_good_habits) {

    private val adapter: MainAdapter by lazy {
        val data = ArrayList<Habit>()
        MainAdapter(data, requireContext())
    }

    private val habitsLiveData by lazy {
        FakeDatabase.habitsLiveData
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerGoodHabits.adapter = adapter
        recyclerGoodHabits.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )

        habitsLiveData.observe(viewLifecycleOwner) { habitsList ->
            adapter.addListOfHabits((habitsList.filter { it.type.toBoolean()}.toMutableList()))
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun Int.toBoolean(): Boolean = this == 1

    companion object {
        const val DISTANCE_BETWEEN_ELEMENTS = 50

        fun newInstance(): FragmentGoodHabits = FragmentGoodHabits()
    }

}