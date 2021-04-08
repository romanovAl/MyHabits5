package com.example.myhabits3.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
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

    val viewModel: MainViewModel by viewModels({ requireParentFragment() }) {
        viewModelFactory
    }

    val adapter: MainAdapter by lazy {
        val data = ArrayList<Habit>()
        MainAdapter(data) { habit ->
            viewModel.addDoneTimes(habit)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerHabits.adapter = adapter
        recyclerHabits.addItemDecoration(
            SpacingItemDecoration(
                DISTANCE_BETWEEN_ELEMENTS
            )
        )

        viewModel.messageWithUndoEvent.observe(viewLifecycleOwner) { messageAndHabit ->
            messageAndHabit?.let { pair ->
                Snackbar.make(
                    requireParentFragment().requireView(),
                    pair.second,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        viewModel.removeLastDoneTimes(pair.first)
                    }.show()
            }
        }

        viewModel.messageWithoutUndoEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(
                    requireParentFragment().requireView(),
                    it,
                    Snackbar.LENGTH_LONG
                )
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
