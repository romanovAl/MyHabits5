package com.example.myhabits3.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myhabits3.model.Habit

class FakeDatabase {

    companion object {

        val habitsList : MutableList<Habit> = mutableListOf()

        fun addHabit(newHabit: Habit) {
            habitsList.add(newHabit)
        }

        fun replaceHabit(oldHabit: Habit, newHabit: Habit) {
            val index = habitsList.indexOf(oldHabit)
            habitsList.removeAt(index)
            habitsList.add(index, newHabit)
        }

    }

}