package com.example.myhabits3.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.myhabits3.model.Habit

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
        return when {
            first.bdId != sec.bdId -> false
            first.title != sec.title -> false
            first.description != sec.title -> false
            first.priority != sec.priority -> false
            first.type != sec.type -> false
            first.count != sec.count -> false
            first.frequency != sec.frequency -> false
            first.color != sec.color -> false
            first.date != sec.date -> false
            first.doneDates != sec.doneDates -> false
            else -> true
        }
    }
}