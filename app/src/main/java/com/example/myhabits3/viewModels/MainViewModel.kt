package com.example.myhabits3.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myhabits3.data.FakeDatabase
import com.example.myhabits3.model.FilterTypes
import com.example.myhabits3.model.Habit

class MainViewModel : ViewModel() {


    private val currentHabitsLiveData: MutableLiveData<MutableList<Habit>> = MutableLiveData()
    val currentHabits: LiveData<MutableList<Habit>> get() = currentHabitsLiveData

    init {
        currentHabitsLiveData.value = FakeDatabase.habitsList
    }

    fun addHabit(newHabit: Habit) {
        cleanHabitsFilter()
        FakeDatabase.addHabit(newHabit)
    }

    fun replaceHabit(oldHabit: Habit, newHabit: Habit) {
        cleanHabitsFilter()
        FakeDatabase.replaceHabit(oldHabit, newHabit)
    }

    fun sortHabits(filterType: FilterTypes, byDescending: Boolean) {

        when (filterType) { //Сортировка по типам

            FilterTypes.ByPriority -> {
                if (byDescending) {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedByDescending { it.priority }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                } else {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedBy { it.priority }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                }
            }

            FilterTypes.ByPeriod -> {
                if (byDescending) {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedByDescending { it.frequency }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                } else {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedBy { it.frequency }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                }
            }

            FilterTypes.ByCount -> {
                if (byDescending) {
                    val sortedHabits = currentHabitsLiveData.value?.sortedByDescending { it.count }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                } else {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedBy { it.count }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                }
            }

            FilterTypes.ByDate -> {
                if (byDescending) {
                    val sortedHabits = currentHabitsLiveData.value?.sortedByDescending { it.date }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                } else {
                    val sortedHabits =
                        currentHabitsLiveData.value?.sortedBy { it.date }
                    currentHabitsLiveData.value = sortedHabits?.toMutableList()
                }
            }

            FilterTypes.NoFilter -> {
                cleanHabitsFilter()
            }
        }
    }

    fun sortHabits(text: String) { //Поиск по привычкам
        if (text.isNotEmpty()) {
            val sortedHabits = FakeDatabase.habitsList.filter {
                it.title.contains(text, ignoreCase = true)
            }
            currentHabitsLiveData.value = sortedHabits.toMutableList()
        } else {
            cleanHabitsFilter()
        }
    }

    fun cleanHabitsFilter() {
        currentHabitsLiveData.value = FakeDatabase.habitsList
    }

}