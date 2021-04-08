package useCases.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import repository.AppRepository
import ru.romanoval.domain.model.ResponseData
import javax.inject.Inject

class DownloadHabitsFromApiUseCase @Inject constructor(
    private val appRepository: AppRepository
) {

    suspend fun execute(): Flow<ResponseData> = flow {
        val habitsFromApiResponse = appRepository.getHabitsFromApi()
        if (habitsFromApiResponse.isSuccessful) {
            appRepository.deleteAllHabitsFromDB()
            habitsFromApiResponse.habitsList?.let { habitsFromApi ->
                appRepository.insertHabitsIntoDB(habitsFromApi.asReversed())
                emit(ResponseData(null, true))

            }
        } else {
            emit(habitsFromApiResponse)
        }
    }

}