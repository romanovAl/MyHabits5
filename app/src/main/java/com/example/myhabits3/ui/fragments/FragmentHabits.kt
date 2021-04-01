package com.example.myhabits3.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myhabits3.R
import com.example.myhabits3.ui.adapters.MainAdapter
import com.example.myhabits3.ui.utils.SpacingItemDecoration
import com.example.myhabits3.ui.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_habits.*
import ru.romanoval.domain.model.Habit
import javax.inject.Inject

open class FragmentHabits : DaggerFragment(R.layout.fragment_habits) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: MainViewModel by activityViewModels {
        viewModelFactory
    }

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
