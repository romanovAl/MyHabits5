package com.example.myhabits3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myhabits3.model.Habit
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_recycler_element.view.*


class MainAdapter(
    var habits: ArrayList<Habit>,
    context: Context,
    val adapterOnClickConstraint: (Habit, Int) -> Unit
) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private val priorities = context.resources.getStringArray(R.array.priorities)
    private val periods = context.resources.getStringArray(R.array.periods)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((inflater.inflate(R.layout.main_recycler_element, parent, false)))
    }

    override fun getItemCount(): Int = habits.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(habits[position], position)
    }

    fun addItem(hab: Habit) {
        habits.add(hab)
        notifyItemInserted(itemCount - 1)
    }

    fun changeItem(hab: Habit, pos: Int) {
        habits[pos] = hab
        notifyItemChanged(pos)
    }

    fun addListOfHabits(newHabits : ArrayList<Habit>){
        habits = newHabits
        notifyDataSetChanged()
    }

    inner class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(habit: Habit, position: Int) {

            containerView.run {

                constraintMainRecyclerElement.setOnClickListener {
                    adapterOnClickConstraint(habit, position)
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

                habitTypeRecyclerElement.text = if (habit.type == 1) {
                    this.resources.getText(R.string.good_habit)
                } else {
                    this.resources.getText(R.string.bad_habit)
                } //TODO когда появится viewPager, убрать отображение типа привычки

            }


        }
    }

}