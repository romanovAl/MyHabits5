package com.example.myhabits3.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myhabits3.R
import com.example.myhabits3.data.AppDatabase
import com.example.myhabits3.utils.FilterTypes
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.HabitMapper
import com.example.myhabits3.repository.Repository
import com.example.myhabits3.restful.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import java.lang.Exception
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val currentHabitsLiveData: MutableLiveData<MutableList<Habit>> = MutableLiveData()
    val currentHabits: LiveData<MutableList<Habit>> get() = currentHabitsLiveData

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = isLoadingLiveData

    private val messageLiveData: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> get() = messageLiveData

    private val messageWithoutUndoLiveData: MutableLiveData<String> = MutableLiveData()
    val messageWithoutUndo: LiveData<String> get() = messageWithoutUndoLiveData

    private val appDatabase = AppDatabase.getHabitsDatabase(getApplication())
    private val repository = Repository(
        appDatabase.habitsDao(),
        ApiService.create()
    )

    private var currentFilterType: FilterTypes = FilterTypes.NoFilter
    private var currentByDescending: Boolean = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentHabitsLiveData.postValue(repository.getHabitsFromDB().toMutableList())
        }
    }

    fun addHabit(newHabit: Habit) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertHabitIntoDB(newHabit)
            cleanHabitsFilter()
        }

    }

    fun replaceHabit(newHabit: Habit) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateHabitInDB(newHabit)
            cleanHabitsFilter()
        }

    }

    fun deleteHabit(habitToDelete: Habit) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHabitFromDB(habitToDelete)
            cleanHabitsFilter()
        }
    }

    fun sortHabits(filterType: FilterTypes, byDescending: Boolean) {

        viewModelScope.launch(Dispatchers.IO) {
            currentFilterType = filterType
            currentByDescending = byDescending

            currentHabitsLiveData.value?.let {
                val sortedHabits = sort(it, filterType, byDescending)
                currentHabitsLiveData.postValue(sortedHabits.toMutableList())
            }
        }

    }

    fun sortHabits(text: String) { //Поиск по привычкам
        viewModelScope.launch(Dispatchers.IO) {
            if (text.isNotEmpty()) {
                searchSortedHabits(text)
            } else {
                if (currentFilterType != FilterTypes.NoFilter) {
                    val sortedHabits = sort(
                        repository.getHabitsFromDB().toMutableList(),
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

    private suspend fun searchSortedHabits(text: String) {
        var sortedHabits = repository.getHabitsFromDB().filter {
            it.title.contains(text, ignoreCase = true)
        }
        if (currentFilterType != FilterTypes.NoFilter) {
            sortedHabits =
                sort(sortedHabits.toMutableList(), currentFilterType, currentByDescending)
        }
        currentHabitsLiveData.postValue(sortedHabits.toMutableList())
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
                repository.getHabitsFromDB().toMutableList()
            }
        }

    }

    suspend fun cleanHabitsFilter() {
        currentFilterType = FilterTypes.NoFilter
        currentByDescending = false
        currentHabitsLiveData.postValue(repository.getHabitsFromDB().toMutableList())
    }


    fun addDoneTimes(habit: Habit) {
        habit.doneDates.add(Calendar.getInstance().timeInMillis)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateHabitInDB(habit)
        }
        calculate(habit, true)
    }

    fun removeLastDoneTimes(habit: Habit) {
        habit.doneDates.removeLast()
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateHabitInDB(habit)
        }
        calculate(habit, false)
    }

    private fun calculate(habit: Habit, withUndo: Boolean) {

        val currentTime = Calendar.getInstance().time.time

        when (habit.frequency) {
            0 -> { //per hour
                val from = currentTime - HOUR
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                displayMessage(habit.count, curCount, withUndo)

            }
            1 -> { //per day
                val from = currentTime - DAY
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                displayMessage(habit.count, curCount, withUndo)
            }
            2 -> {//per week
                val from = currentTime - WEEK
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                displayMessage(habit.count, curCount, withUndo)
            }
            3 -> {//per month
                val from = currentTime - MONTH
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                displayMessage(habit.count, curCount, withUndo)
            }
            4 -> {//per year
                val from = currentTime - YEAR
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                displayMessage(habit.count, curCount, withUndo)
            }
        }
    }

    private fun displayMessage(count: Int, curCount: Int, withUndo: Boolean) {
        when {
            curCount < count -> {
                if (withUndo) {
                    messageLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_have_to_do_this_habit,
                            (count - curCount).toString()
                        )
                } else {
                    messageWithoutUndoLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_have_to_do_this_habit,
                            (count - curCount).toString()
                        )
                }
            }
            curCount > count -> {
                if (withUndo) {
                    messageLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_have_already_done_enough
                        )
                } else {
                    messageWithoutUndoLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_have_already_done_enough
                        )
                }
            }
            else -> {
                if (withUndo) {
                    messageLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_are_breathtaking
                        )
                } else {
                    messageWithoutUndoLiveData.value =
                        getApplication<Application>().resources.getString(
                            R.string.you_are_breathtaking
                        )
                }


            }
        }
        messageLiveData.value = null
        messageWithoutUndoLiveData.value = null

    }

    fun insertHabitsIntoApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)

            try {
                var habitsFromApi = repository.getHabitsFromApi() //Удаляем всё, что есть в апи
                repository.deleteHabitsFromApi(habitsFromApi)

                val habitsFromDb = repository.getHabitsFromDB().map {
                    HabitMapper().habitToServerHabit(it)
                }
                repository.insertHabitsIntoApi( //Вставляем актуальные привычки
                    habitsFromDb
                )
                habitsFromApi = repository.getHabitsFromApi()

                repository.postHabitsDone(habitsFromApi, habitsFromDb) //Кладем done_dates в апи

                isLoadingLiveData.postValue(false)
            } catch (e: HttpException) {

                when (e.code()){

                    400 -> println("error 400 - Bad request!")
                    401 -> println("error 401 - Unauthorized exception")
                    403 -> println("error 403 - Forbidden exception")
                    404 -> println("error 404 - Not found")
                    409 -> println("error 409 - Conflict exception")
                    500 -> println("error 500 - internal server error")
                    503 -> println("error 503 - service unavailable")

                }

                //Попробовать call

                e.response()?.errorBody()?.let{

                }

                isLoadingLiveData.postValue(false)
            }

        }

    }

    fun downloadHabitsFromApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)

            try {
                repository.deleteAllHabitsFromDB()

                val habitsFromApi = repository.getHabitsFromApi().map {
                    HabitMapper().serverHabitToHabit(it)
                }

                println(habitsFromApi)

                repository.insertHabitsIntoDB(habitsFromApi.asReversed())

                currentHabitsLiveData.postValue(repository.getHabitsFromDB().toMutableList())

                isLoadingLiveData.postValue(false)
            } catch (e: HttpException) {
                isLoadingLiveData.postValue(true)
            }


        }

    }

    companion object {
        const val HOUR = 3600000
        const val DAY = 86400000
        const val WEEK = 604800000
        const val MONTH = 2592000000
        const val YEAR = 31536000000
    }

}