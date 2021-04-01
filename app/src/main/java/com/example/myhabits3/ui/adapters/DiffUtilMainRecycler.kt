package com.example.myhabits3.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.romanoval.domain.model.Habit

class DiffUtilMainRecycler(
    private val oldList: List<Habit>,
    private val newList: List<Habit>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val first = oldList[oldItemPosition]
        val sec = newList[newItemPosition]
        return first == sec
    }
}