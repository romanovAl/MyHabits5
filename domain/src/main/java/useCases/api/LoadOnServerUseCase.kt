package useCases.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import repository.AppRepository
import ru.romanoval.domain.model.ResponseData
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid
import javax.inject.Inject

class LoadOnServerUseCase @Inject constructor(
    private val appRepository: AppRepository
) {

    suspend fun execute(): Flow<ResponseData> = flow {

        val deleteResponse = deleteAllFromApi()
        if (!deleteResponse.isSuccessful) {
            emit(deleteResponse)
        } else {
            val loadResponse = loadHabitsFromDBIntoApi()
            if (!loadResponse.isSuccessful) {
                emit(loadResponse)
            } else {
                val postHabitsDoneResponse = collectAndPostDoneDates()
                if (!postHabitsDoneResponse.isSuccessful) {
                    emit(postHabitsDoneResponse)
                } else {
                    emit(ResponseData(null, true))
                }
            }
        }
    }

    private suspend fun collectAndPostDoneDates(): ResponseData {
        val habitsFromDB = appRepository.selectAllHabitsFromDB()
        val postDones: MutableList<PostDone> = mutableListOf()
        habitsFromDB.forEach { habit ->  //Собираем все doneDates и делаем из них postDones
            habit.doneDates.forEach { doneDate ->
                habit.uid?.let { uid ->
                    postDones.add(PostDone(uid, doneDate))
                }
            }
        }
        return appRepository.postHabitsDone(postDones)
    }

    private suspend fun deleteAllFromApi(): ResponseData {
        val habitsFromApiResponse = appRepository.getHabitsFromApi()
        if (habitsFromApiResponse.isSuccessful) { //Удаляем всё с апи
            habitsFromApiResponse.habitsList?.let { habitsFromApi ->
                habitsFromApi.forEach { serverHabit ->
                    serverHabit.uid?.let {
                        val deleteResponse = appRepository.deleteHabitFromApi(Uid(it))
                        if (!deleteResponse.isSuccessful) {
                            return deleteResponse
                        }
                    }
                }
            }
        } else {
            return habitsFromApiResponse
        }

        return ResponseData(null, true)
    }

    private suspend fun loadHabitsFromDBIntoApi(): ResponseData {
        val habitsFromDB = appRepository.selectAllHabitsFromDB()
        habitsFromDB.forEach { habit ->
            val insertResponse = appRepository.putHabitIntoApi(habit)
            if (insertResponse.isSuccessful) {
                insertResponse.uid?.let { uidFromServer ->
                    val updatedHabit = habit.copy() //Возможно лишнее
                    updatedHabit.uid = uidFromServer.uid
                    appRepository.updateHabitInDB(updatedHabit)
                }
            } else {
                return insertResponse
            }
        }

        return ResponseData(null, true)
    }

}