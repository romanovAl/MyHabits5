package com.example.myhabits3.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.myhabits3.R
import com.example.myhabits3.ui.utils.FilterTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Retrofit
import ru.romanoval.data.mapper.ErrorConverter
import ru.romanoval.data.mapper.HabitMapper
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.restful.Error
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid
import useCases.api.DeleteHabitFromApiUseCase
import useCases.api.GetHabitsFromApiUseCase
import useCases.api.PostHabitDoneUseCase
import useCases.api.PutHabitApiUseCase
import useCases.database.*
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getHabitsFromDBUseCase: GetHabitsFromDBUseCase,
    private val insertHabitIntoDBUseCase: InsertHabitIntoDBUseCase,
    private val insertHabitsIntoDBUseCase: InsertHabitsIntoDBUseCase,
    private val updateHabitInDBUseCase: UpdateHabitInDBUseCase,
    private val deleteHabitFromDBUseCase: DeleteHabitFromDBUseCase,
    private val deleteAllHabitsFromDBUseCase: DeleteAllHabitsFromDBUseCase,
    private val getHabitsFromApiUseCase: GetHabitsFromApiUseCase,
    private val putHabitApiUseCase: PutHabitApiUseCase,
    private val postHabitDoneUseCase: PostHabitDoneUseCase,
    private val deleteHabitFromApiUseCase: DeleteHabitFromApiUseCase,
    private val habitMapper: HabitMapper,
    private val errorConverter: ErrorConverter,
    application: Application
) : AndroidViewModel(application) {


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

    private var currentFilterType: FilterTypes = FilterTypes.NoFilter
    private var currentByDescending: Boolean = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentHabitsLiveData.postValue(getHabitsFromDBUseCase.execute().toMutableList())
        }
    }

    fun addHabit(newHabit: Habit) {

        viewModelScope.launch(Dispatchers.IO) {
            insertHabitIntoDBUseCase.execute(newHabit)
            cleanHabitsFilter()
        }

    }

    fun replaceHabit(newHabit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            updateHabitInDBUseCase.execute(newHabit)
            cleanHabitsFilter()
        }
    }

    fun deleteHabit(habitToDelete: Habit) {

        viewModelScope.launch(Dispatchers.IO) {
            deleteHabitFromDBUseCase.execute(habitToDelete)
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
                        getHabitsFromDBUseCase.execute().toMutableList(),
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
        var sortedHabits = getHabitsFromDBUseCase.execute().filter {
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
                getHabitsFromDBUseCase.execute().toMutableList()
            }
        }

    }

    suspend fun cleanHabitsFilter() {
        currentFilterType = FilterTypes.NoFilter
        currentByDescending = false
        currentHabitsLiveData.postValue(getHabitsFromDBUseCase.execute().toMutableList())
    }


    fun addDoneTimes(habit: Habit) {
        habit.doneDates.add(Calendar.getInstance().timeInMillis)
        viewModelScope.launch(Dispatchers.IO) {
            updateHabitInDBUseCase.execute(habit)
        }
        calculate(habit, true)
    }

    fun removeLastDoneTimes(habit: Habit) {
        habit.doneDates.removeLast()
        viewModelScope.launch(Dispatchers.IO) {
            updateHabitInDBUseCase.execute(habit)
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
            val response = putHabitApiUseCase.execute(
                habitMapper.habitToServerHabit(
                    habitMapper.habitDomainToHabitData(habit)
                )
            )
            if (response.isSuccessful) {

                response.body()?.let {
                    habit.uid = it.uid
                    updateHabitInDBUseCase.execute(habit)
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

    private fun convertError(errorBody: ResponseBody?): Error? =
        errorConverter.convertError(errorBody)

    fun insertHabitsIntoApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)

            try {
                val habitsFromApiResponse = getHabitsFromApiUseCase.execute()

                if (habitsFromApiResponse.isSuccessful) {
                    habitsFromApiResponse.body()?.let { habitsFromApi ->

                        habitsFromApi.forEach {  //Удаляем всё, что есть в апи
                            val uid = Uid(it.uid!!)
                            val deleteResponse = deleteHabitFromApiUseCase.execute(uid)
                            if (!deleteResponse.isSuccessful) {
                                val error = convertError(deleteResponse.errorBody())
                                error?.let { err ->
                                    apiErrorLiveData.postValue("error ${err.code} - ${err.message}")
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

                val habitsFromDb = getHabitsFromDBUseCase.execute()

                val putResult =
                    putHabitsIntoApiAndChangeUid(habitsFromDb) //Получаем привычки и меняем Uid у текущих в бд

                if (!putResult) { //Если мы не смогли что-то положить в апи
                    //Ошибка
                    isLoadingLiveData.postValue(false)
                    return@launch

                }

                getHabitsFromDBUseCase.execute().forEach { habit ->
                    habit.uid?.let { uid ->
                        habit.doneDates.forEach { doneDate ->
                            val postHabitDoneResponse = postHabitDoneUseCase.execute(
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


                val habitsFromApiResponse =
                    getHabitsFromApiUseCase.execute()//Получаем привычки из апи
                if (habitsFromApiResponse.isSuccessful) {
                    deleteAllHabitsFromDBUseCase.execute() //Удаляем все привычки из БД

                    habitsFromApiResponse.body()?.let { habitsFromApi ->
                        val dataHabits = habitsFromApi.map {
                            habitMapper.serverHabitToHabit(it)
                        }.asReversed()
                        insertHabitsIntoDBUseCase.execute(
                            dataHabits.map {
                                habitMapper.habitDataToHabitDomain(it)
                            }
                        )
                        currentHabitsLiveData.postValue(
                            getHabitsFromDBUseCase.execute().toMutableList()
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