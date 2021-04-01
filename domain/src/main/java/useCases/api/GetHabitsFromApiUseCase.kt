package useCases.api

import kotlinx.coroutines.flow.Flow
import repository.AppRepository
import retrofit2.Response
import ru.romanoval.domain.model.restful.ServerHabit
import javax.inject.Inject

class GetHabitsFromApiUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(): Response<List<ServerHabit>> =
        appRepository.getHabitsFromApi()
}