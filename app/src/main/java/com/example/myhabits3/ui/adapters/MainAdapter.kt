package com.example.myhabits3.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myhabits3.R
import com.example.myhabits3.ui.fragments.MainFragmentDirections
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_recycler_element.view.*
import ru.romanoval.domain.model.Habit


class MainAdapter(
    private var habits: MutableList<Habit>,
    val adapterOnClickDone: (Habit) -> Unit
) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((inflater.inflate(R.layout.main_recycler_element, parent, false)))
    }

    override fun getItemCount(): Int = habits.size

    fun setData(newHabits: MutableList<Habit>){
        val diffUtil = DiffUtilMainRecycler(habits,newHabits)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        habits = newHabits
        diffResults.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(habits[position])
    }


    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(habit: Habit) {

            containerView.run {

                val priorities = context.resources.getStringArray(R.array.priorities)
                val periods = context.resources.getStringArray(R.array.periods)

                constraintMainRecyclerElement.setOnClickListener {
                    val action = MainFragmentDirections.actionFragmentMainToFragmentAddEdit(
                        context.resources.getString(R.string.label_edit)
                    )
                    action.habitToEdit = habit

                    Navigation.findNavController(it).navigate(action)
                }

                habitNameRecyclerElement.text = habit.title
                habitDescriptionRecyclerElement.text = habit.description
                habitPeriodRecyclerElement.text = when (habit.frequency) {
                    0 -> "${habit.count} ${resources.getString(R.string.times)} ${periods[0]}"
                    1 -> "${habit.count} ${resources.getString(R.string.times)} ${periods[1]}"
                    2 -> "${habit.count} ${resources.getString(R.string.times)} ${periods[2]}"
                    3 -> "${habit.count} ${resources.getString(R.string.times)} ${periods[3]}"
                    else -> "${habit.count} ${resources.getString(R.string.times)} ${periods[4]}"
                }
                habitPriorityRecyclerElement.text = when (habit.priority) {
                    1 -> "${priorities[1]} ${this.resources.getString(R.string.priority)}"
                    2 -> "${priorities[2]} ${this.resources.getString(R.string.priority)}"
                    else -> priorities[0]
                }
                habitColorDivider.setBackgroundColor(habit.color)

                buttonDone.setOnClickListener {
                    adapterOnClickDone(habit)
                }

            }


        }
    }

}