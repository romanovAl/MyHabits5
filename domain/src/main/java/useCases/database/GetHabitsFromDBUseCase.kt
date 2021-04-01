package useCases.database

import kotlinx.coroutines.flow.Flow
import repository.AppRepository
import ru.romanoval.domain.model.Habit
import javax.inject.Inject

class GetHabitsFromDBUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(): List<Habit> =
        appRepository.selectAllHabits()
}