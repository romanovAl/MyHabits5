package useCases.database

import repository.AppRepository
import ru.romanoval.domain.model.Habit
import javax.inject.Inject

class UpdateHabitInDBUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(habit: Habit) =
        appRepository.updateHabitInDB(habit)
}