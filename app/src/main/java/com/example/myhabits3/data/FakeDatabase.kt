package com.example.myhabits3.data

import androidx.lifecycle.MutableLiveData
import com.example.myhabits3.model.Habit

class FakeDatabase {

    companion object {

        var habitsLiveData: MutableLiveData<MutableList<Habit>> = MutableLiveData()

        init {
            habitsLiveData.value = mutableListOf()
        }

        fun addHabit(newHabit: Habit) {
            habitsLiveData.value!!.add(newHabit)
        }

        fun replaceHabit(oldHabit: Habit, newHabit: Habit) {
            habitsLiveData.value?.let{value ->
                val index = value.indexOf(oldHabit)
                value.removeAt(index)
                value.add(index,newHabit)
            }

        }

    }

}