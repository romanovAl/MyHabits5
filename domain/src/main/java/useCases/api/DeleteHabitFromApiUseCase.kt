package useCases.api

import kotlinx.coroutines.flow.Flow
import repository.AppRepository
import retrofit2.Response
import ru.romanoval.domain.model.restful.Uid
import javax.inject.Inject

class DeleteHabitFromApiUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(uid: Uid): Response<Unit> =
        appRepository.deleteHabitFromApi(uid)
}