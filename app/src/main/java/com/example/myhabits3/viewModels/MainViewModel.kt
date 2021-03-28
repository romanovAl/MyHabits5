package com.example.myhabits3.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myhabits3.R
import com.example.myhabits3.data.AppDatabase
import com.example.myhabits3.model.*
import com.example.myhabits3.utils.FilterTypes
import com.example.myhabits3.repository.Repository
import com.example.myhabits3.restful.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
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

    private val apiErrorLiveData: MutableLiveData<String> = MutableLiveData()
    val apiError: LiveData<String> get() = apiErrorLiveData

    private val appDatabase = AppDatabase.getHabitsDatabase(getApplication())

    private val retrofit = ApiService.create()
    private val repository = Repository(
        appDatabase.habitsDao(),
        retrofit.create(ApiService::class.java)
    )

    private var currentFilterType: FilterTypes = FilterTypes.NoFilter
    private var currentByDescending: Boolean = false

    private val habitMapper = HabitMapper()

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

    private suspend fun putHabitsIntoApiAndChangeUid(habits: List<Habit>): Boolean {

        habits.forEach { habit ->
            val response = repository.insertHabitIntoApi(habitMapper.habitToServerHabit(habit))
            if (response.isSuccessful) {

                response.body()?.let {
                    habit.uid = it.uid
                    repository.updateHabitInDB(habit)
                }

            } else {
                val error = convertError(response.errorBody())
                error?.let {
                    isLoadingLiveData.postValue(false)
                    apiErrorLiveData.postValue("error ${it.code} - ${it.message}")
                }
                return false
            }
        }
        return true
    }

    private fun convertError(errorBody: ResponseBody?): Error? {
        errorBody?.let { responseBody ->

            return retrofit.responseBodyConverter<Error>(Error::class.java, arrayOf())
                .convert(responseBody)
        }

        return null

    }

    fun insertHabitsIntoApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)

            try {
                val habitsFromApiResponse = repository.getHabitsFromApi()

                if (habitsFromApiResponse.isSuccessful) {
                    habitsFromApiResponse.body()?.let { habitsFromApi ->

                        habitsFromApi.forEach {  //Удаляем всё, что есть в апи
                            val uid = Uid(it.uid!!)
                            val deleteResponse = repository.deleteHabitFromApi(uid)
                            if (!deleteResponse.isSuccessful) {
                                val error = convertError(deleteResponse.errorBody())
                                error?.let {
                                    apiErrorLiveData.postValue("error ${it.code} - ${it.message}")
                                }
                                isLoadingLiveData.postValue(false)
                                return@launch
                            }
                        }
                    }
                } else {
                    val error = convertError(habitsFromApiResponse.errorBody())
                    error?.let {
                        apiErrorLiveData.postValue("error ${it.code} - ${it.message}")
                    }
                    isLoadingLiveData.postValue(false)
                    return@launch
                }

                val habitsFromDb = repository.getHabitsFromDB()

                val putResult =
                    putHabitsIntoApiAndChangeUid(habitsFromDb) //Получаем привычки и меняем Uid у текущих в бд

                if (!putResult) { //Если мы не смогли что-то положить в апи
                    //Ошибка
                    isLoadingLiveData.postValue(false)
                    return@launch

                }

                repository.getHabitsFromDB().forEach { habit ->
                    habit.uid?.let { uid ->
                        habit.doneDates.forEach { doneDate ->
                            val postHabitDoneResponse = repository.postHabitDone(
                                PostDone(
                                    uid,
                                    doneDate
                                )
                            ) //Постим done_dates
                            if (!postHabitDoneResponse.isSuccessful) {
                                val error = convertError(postHabitDoneResponse.errorBody())
                                error?.let {
                                    apiErrorLiveData.postValue("error ${it.code} - ${it.message}")
                                }
                                isLoadingLiveData.postValue(false)
                                return@launch
                            }
                        }

                    }

                }

                isLoadingLiveData.postValue(false)
            } catch (e: Exception) {

                if (e is HttpException) {
                    apiErrorLiveData.postValue("error ${e.code()} - ${e.message()}")

                    isLoadingLiveData.postValue(false)
                } else {
                    delay(5000) //Если случилась непредвиденная ошибка, то повторяем запросы
                    insertHabitsIntoApi()
                    //TODO сделать счетчик запросов
                }

                isLoadingLiveData.postValue(false)
            }

        }

    }

    fun downloadHabitsFromApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)

            try {


                val habitsFromApiResponse = repository.getHabitsFromApi() //Получаем привычки из апи
                if (habitsFromApiResponse.isSuccessful) {
                    repository.deleteAllHabitsFromDB() //Удаляем все привычки из БД

                    habitsFromApiResponse.body()?.let { habitsFromApi ->
                        repository.insertHabitsIntoDB(
                            habitsFromApi.map {
                                habitMapper.serverHabitToHabit(it)
                            }.asReversed()
                        )
                        currentHabitsLiveData.postValue(
                            repository.getHabitsFromDB().toMutableList()
                        )

                        isLoadingLiveData.postValue(false)
                    }

                } else {
                    val error = convertError(habitsFromApiResponse.errorBody())
                    error?.let {
                        apiErrorLiveData.postValue("error ${it.code} - ${it.message}")
                    }
                    isLoadingLiveData.postValue(false)
                    return@launch
                }

            } catch (e: Exception) {

                if (e is HttpException) {
                    apiErrorLiveData.postValue("error ${e.code()} - ${e.message()}")

                    isLoadingLiveData.postValue(false)
                } else {
                    println(e.message)
                    delay(5000)
                    downloadHabitsFromApi()
                }


                isLoadingLiveData.postValue(false)
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