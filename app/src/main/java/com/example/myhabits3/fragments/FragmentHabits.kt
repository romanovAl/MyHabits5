package com.example.myhabits3.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myhabits3.R
import com.example.myhabits3.adapters.MainAdapter
import com.example.myhabits3.model.Habit
import com.example.myhabits3.utils.SpacingItemDecoration
import com.example.myhabits3.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_habits.*

open class FragmentHabits : Fragment(R.layout.fragment_habits) {

    val viewModel: MainViewModel by activityViewModels()

    var habitFromAdapter: Habit? = null

    val adapter: MainAdapter by lazy {
        val data = ArrayList<Habit>()
        MainAdapter(data, requireContext()) { habit ->
            viewModel.addDoneTimes(habit)

            habitFromAdapter = habit
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerHabits.adapter = adapter
        recyclerHabits.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {

                Snackbar.make(requireParentFragment().requireView(), message, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        habitFromAdapter?.let {
                            viewModel.removeLastDoneTimes(habitFromAdapter!!)
                        }
                    }.show()
            }
        }

        viewModel.messageWithoutUndo.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(requireParentFragment().requireView(), message, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun Int.toBoolean(): Boolean = this == 1

    companion object {
        const val DISTANCE_BETWEEN_ELEMENTS = 50
    }

}
