package com.example.myhabits3.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myhabits3.model.Habit

class SortedHabitsStorage {

    companion object{

        private val sortedHabitsLiveData : MutableLiveData<List<Habit>> = MutableLiveData()
        val sortedHabits : LiveData<List<Habit>> get() = sortedHabitsLiveData

        init {
            sortedHabitsLiveData.value = mutableListOf()
        }

        fun setSortedHabits(sortedHabits : List<Habit>){
            sortedHabitsLiveData.value = sortedHabits
        }

    }

}