package com.example.myhabits3.ui.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.myhabits3.R
import com.example.myhabits3.ui.utils.FilterTypes
import com.example.myhabits3.ui.utils.LiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import retrofit2.HttpException
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.HabitState
import useCases.doneDates.CalculateDoneDateUseCase
import useCases.api.DownloadHabitsFromApiUseCase
import useCases.api.LoadOnServerUseCase
import useCases.database.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getHabitsFromDBUseCase: GetHabitsFromDBUseCase,
    private val insertHabitIntoDBUseCase: InsertHabitIntoDBUseCase,
    private val updateHabitInDBUseCase: UpdateHabitInDBUseCase,
    private val deleteHabitFromDBUseCase: DeleteHabitFromDBUseCase,
    private val loadOnServerUseCase: LoadOnServerUseCase,
    private val downloadHabitsFromApiUseCase: DownloadHabitsFromApiUseCase,
    private val calculateDoneDateUseCase: CalculateDoneDateUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val currentHabitsLiveData: MutableLiveData<MutableList<Habit>> = MutableLiveData()
    val currentHabits: LiveData<MutableList<Habit>> get() = currentHabitsLiveData

    private val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = isLoadingLiveData

    val messageWithUndoEvent: LiveEvent<Pair<Habit, String>> = LiveEvent()
    val messageWithoutUndoEvent: LiveEvent<String> = LiveEvent()

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
        viewModelScope.launch(Dispatchers.IO) {
            val state = calculateDoneDateUseCase.execute(habit, true)
            when (state.doneState) {
                HabitState.doneNotEnough -> setMessageWithLessCount(true, habit, state.count)
                HabitState.doneJustEnough -> setMessageWithEqualCount(true, habit)
                HabitState.doneMoreThanNeeded -> setMessageWithMoreCount(true, habit)
            }
        }
    }

    fun removeLastDoneTimes(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = calculateDoneDateUseCase.execute(habit, false)
            when (state.doneState) {
                HabitState.doneNotEnough -> setMessageWithLessCount(false, habit, state.count)
                HabitState.doneJustEnough -> setMessageWithEqualCount(false, habit)
                HabitState.doneMoreThanNeeded -> setMessageWithMoreCount(false, habit)
            }
        }
    }


    private fun setMessageWithEqualCount(
        withUndo: Boolean,
        habit: Habit
    ) {
        if (withUndo) {
            messageWithUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.you_are_breathtaking
                        )
                    )
                } else {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.you_should_not_do_this_habit_more
                        )
                    )
                }
            )
        } else {
            messageWithoutUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    getApplication<Application>().resources.getString(
                        R.string.you_are_breathtaking
                    )
                } else {
                    getApplication<Application>().resources.getString(
                        R.string.you_should_not_do_this_habit_more
                    )
                }
            )
        }
    }

    private fun setMessageWithMoreCount(
        withUndo: Boolean,
        habit: Habit
    ) {
        if (withUndo) {
            messageWithUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.you_have_already_done_enough
                        )
                    )
                } else {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.stop_doing_this_habit
                        )
                    )
                }
            )
        } else {
            messageWithoutUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    getApplication<Application>().resources.getString(
                        R.string.you_have_already_done_enough
                    )
                } else {
                    getApplication<Application>().resources.getString(
                        R.string.stop_doing_this_habit
                    )
                }
            )
        }
    }

    private fun setMessageWithLessCount(
        withUndo: Boolean,
        habit: Habit,
        count: Int
    ) {
        if (withUndo) {
            messageWithUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.you_have_to_do_this_habit,
                            (count).toString()
                        )
                    )
                } else {
                    Pair(
                        habit,
                        getApplication<Application>().resources.getString(
                            R.string.you_can_do_this_habit,
                            (count).toString()
                        )
                    )
                }
            )
        } else {
            messageWithoutUndoEvent.postValue(
                if (habit.type.toBoolean()) {
                    getApplication<Application>().resources.getString(
                        R.string.you_have_to_do_this_habit,
                        (count).toString()
                    )
                } else {
                    getApplication<Application>().resources.getString(
                        R.string.you_can_do_this_habit,
                        (count).toString()
                    )
                }
            )
        }
    }


    fun insertHabitsIntoApi() {
        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)
            loadOnServerUseCase.execute()
                .catch { e ->
                    val needToRetry = catchException(e)
                    if (needToRetry) insertHabitsIntoApi()
                }
                .collect { responseData ->
                    if (!responseData.isSuccessful)
                        apiErrorLiveData.postValue(responseData.errorMessage!!)
                    isLoadingLiveData.postValue(false)
                }
        }
    }

    fun downloadHabitsFromApi() {

        viewModelScope.launch(Dispatchers.IO) {

            isLoadingLiveData.postValue(true)
            downloadHabitsFromApiUseCase.execute()
                .catch { e ->
                    val needToRetry = catchException(e)
                    if (needToRetry) downloadHabitsFromApi()
                }
                .collect { responseData ->
                    if (!responseData.isSuccessful)
                        apiErrorLiveData.postValue(responseData.errorMessage)
                    isLoadingLiveData.postValue(false)
                }

            cleanHabitsFilter()
        }
    }

    private suspend fun catchException(e: Throwable): Boolean {
        var needToRetry = false
        if (e is HttpException) {
            apiErrorLiveData.postValue("error ${e.code()} - ${e.message()}")
            delay(5000) //Если случилась ошибка сети, повторяем запрос, мало ли что
            needToRetry = true
        } else {
            apiErrorLiveData.postValue("Что-то пошло не так, передайте разработчику, что он криворукий")
            e.printStackTrace()
            isLoadingLiveData.postValue(false)
        }
        return needToRetry
    }

    private fun Int.toBoolean(): Boolean = this == 1

}