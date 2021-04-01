package useCases.api

import kotlinx.coroutines.flow.Flow
import repository.AppRepository
import retrofit2.Response
import ru.romanoval.domain.model.restful.ServerHabit
import ru.romanoval.domain.model.restful.Uid
import javax.inject.Inject

class PutHabitApiUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(habit: ServerHabit): Response<Uid> =
        appRepository.insertHabitIntoApi(habit)
}