package useCases.api

import kotlinx.coroutines.flow.Flow
import repository.AppRepository
import retrofit2.Response
import ru.romanoval.domain.model.restful.PostDone
import javax.inject.Inject

class PostHabitDoneUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(postDone: PostDone): Response<Unit> =
        appRepository.postHabitDone(postDone)
}