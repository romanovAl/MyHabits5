package useCases.database

import repository.AppRepository
import ru.romanoval.domain.model.Habit
import javax.inject.Inject

class InsertHabitsIntoDBUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(habits: List<Habit>) =
        habits.forEach { habit ->
            appRepository.insertHabitIntoDB(habit)
        }
}