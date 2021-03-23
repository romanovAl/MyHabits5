package com.example.myhabits3.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myhabits3.data.AppDatabase
import com.example.myhabits3.model.FilterTypes
import com.example.myhabits3.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val currentHabitsLiveData: MutableLiveData<MutableList<Habit>> = MutableLiveData()
    val currentHabits: LiveData<MutableList<Habit>> get() = currentHabitsLiveData

    private val appDatabase = AppDatabase.getHabitsDatabase(getApplication())
    private val habitsDao = appDatabase.habitsDao()

    private var currentFilterType: FilterTypes = FilterTypes.NoFilter
    private var currentByDescending: Boolean = false

    init {
        viewModelScope.launch(Dispatchers.IO){
            currentHabitsLiveData.postValue(habitsDao.selectAllHabits().toMutableList())
        }
    }

    fun addHabit(newHabit: Habit) {
        cleanHabitsFilter()
        viewModelScope.launch(Dispatchers.IO){
            habitsDao.insertHabit(newHabit)
        }

    }

    fun replaceHabit(newHabit: Habit) {
        cleanHabitsFilter()
        viewModelScope.launch ( Dispatchers.IO ){
            habitsDao.updateHabit(newHabit)
        }

    }

    fun deleteHabit(habitToDelete : Habit){
        cleanHabitsFilter()
        viewModelScope.launch(Dispatchers.IO){
            habitsDao.deleteHabit(habitToDelete)
        }
    }

    fun sortHabits(filterType: FilterTypes, byDescending: Boolean) {

        viewModelScope.launch(Dispatchers.IO){
            currentFilterType = filterType
            currentByDescending = byDescending
            
            currentHabitsLiveData.value?.let {
                val sortedHabits = sort(it, filterType, byDescending)
                currentHabitsLiveData.postValue(sortedHabits.toMutableList())
            }
        }

    }

    fun sortHabits(text: String) { //Поиск по привычкам
        viewModelScope.launch(Dispatchers.IO){
            if (text.isNotEmpty()) {
                var sortedHabits = habitsDao.selectAllHabits().filter {
                    it.title.contains(text, ignoreCase = true)
                }
                if (currentFilterType != FilterTypes.NoFilter) {
                    sortedHabits =
                        sort(sortedHabits.toMutableList(), currentFilterType, currentByDescending)
                }
                currentHabitsLiveData.postValue(sortedHabits.toMutableList())
            } else {
                if (currentFilterType != FilterTypes.NoFilter) {
                    val sortedHabits = sort(
                        habitsDao.selectAllHabits().toMutableList(),
                        currentFilterType,
                        currentByDescending
                    )
                    currentHabitsLiveData.postValue(sortedHabits.toMutableList())
                } else {
                    cleanHabitsFilter()
                }
            }
        }

    }

    private suspend fun sort(
        habitsToSort: MutableList<Habit>,
        filterType: FilterTypes,
        byDescending: Boolean
    ): List<Habit> {

        return when (filterType) {

            FilterTypes.ByPriority -> {
                if (byDescending) {
                    habitsToSort.sortedByDescending { it.priority }.toMutableList()
                } else {
                    habitsToSort.sortedBy { it.priority }.toMutableList()
                }
            }

            FilterTypes.ByPeriod -> {
                if (byDescending) {
                    habitsToSort.sortedByDescending { it.frequency }.toMutableList()
                } else {
                    habitsToSort.sortedBy { it.frequency }.toMutableList()
                }
            }

            FilterTypes.ByCount -> {
                if (byDescending) {
                    habitsToSort.sortedByDescending { it.count }.toMutableList()
                } else {
                    habitsToSort.sortedBy { it.count }.toMutableList()
                }
            }

            FilterTypes.ByDate -> {
                if (byDescending) {
                    habitsToSort.sortedByDescending { it.date }
                } else {
                    habitsToSort.sortedBy { it.date }
                }
            }

            FilterTypes.NoFilter -> {
                habitsDao.selectAllHabits().toMutableList()
            }
        }

    }

    //TODO пофиксить поиск по сортированным привычкам

    fun cleanHabitsFilter() {
        currentFilterType = FilterTypes.NoFilter
        currentByDescending = false
        viewModelScope.launch(Dispatchers.IO){
            currentHabitsLiveData.postValue(habitsDao.selectAllHabits().toMutableList())
        }
    }

}