package useCases.doneDates

import repository.AppRepository
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.HabitState
import ru.romanoval.domain.model.Period
import java.util.*
import javax.inject.Inject

class CalculateDoneDateUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(habit: Habit, add : Boolean): HabitState {
        val doneDate = Calendar.getInstance().timeInMillis
        if(add){
            habit.doneDates.add(doneDate)
        }else{
            habit.doneDates.removeLast()
        }
        appRepository.updateHabitInDB(habit)
        return calculate(habit)
    }

    private fun calculate(habit: Habit): HabitState {

        val currentTime = Calendar.getInstance().time.time

        when (habit.frequency) {
            0 -> { //per hour
                val from = currentTime - Period.HOUR
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                return calculateHabitState(habit, curCount)

            }
            1 -> { //per day
                val from = currentTime - Period.DAY
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                return calculateHabitState(habit, curCount)
            }
            2 -> {//per week
                val from = currentTime - Period.WEEK
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                return calculateHabitState(habit, curCount)
            }
            3 -> {//per month
                val from = currentTime - Period.MONTH
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                return calculateHabitState(habit, curCount)
            }
            else -> {//per year
                val from = currentTime - Period.YEAR
                val curCount = habit.doneDates.filter {
                    (it in from..currentTime)
                }.size
                return calculateHabitState(habit, curCount)
            }
        }
    }

    private fun calculateHabitState(habit: Habit, curCount: Int): HabitState {
        return when {
            curCount < habit.count -> {
                HabitState(HabitState.doneNotEnough, habit.count - curCount)
            }
            curCount > habit.count -> {
                HabitState(HabitState.doneMoreThanNeeded, 0)
            }
            else -> {
                HabitState(HabitState.doneJustEnough, 0)
            }
        }
    }
}