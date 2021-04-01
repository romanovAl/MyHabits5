package useCases.database

import repository.AppRepository
import javax.inject.Inject

class DeleteAllHabitsFromDBUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute() =
        appRepository.deleteAllHabitsFromDB()
}