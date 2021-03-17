package com.example.myhabits3.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myhabits3.data.FakeDatabase
import com.example.myhabits3.data.SortedHabitsStorage
import com.example.myhabits3.model.Habit

class MainViewModel : ViewModel() {

    val habits: LiveData<MutableList<Habit>> = FakeDatabase.habits

    val sortedHabits: LiveData<List<Habit>> = SortedHabitsStorage.sortedHabits

    fun addHabit(newHabit: Habit) {
        FakeDatabase.addHabit(newHabit)
    }

    fun replaceHabit(oldHabit: Habit, newHabit: Habit) {
        FakeDatabase.replaceHabit(oldHabit, newHabit)
    }

    fun setSortedHabits(sortedHabits: List<Habit>) {
        SortedHabitsStorage.setSortedHabits(sortedHabits)
    }

}