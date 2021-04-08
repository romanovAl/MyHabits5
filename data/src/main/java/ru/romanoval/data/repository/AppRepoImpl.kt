package ru.romanoval.data.repository

import repository.AppRepository
import ru.romanoval.data.mapper.ErrorConverter
import ru.romanoval.data.mapper.HabitMapper
import ru.romanoval.data.source.cloud.BaseCloudRepository
import ru.romanoval.data.source.database.HabitsDao
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.ResponseData
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.Uid
import javax.inject.Inject

class AppRepoImpl @Inject constructor(
    private val cloudRepository: BaseCloudRepository,
    private val habitsDao: HabitsDao,
    private val habitMapper: HabitMapper,
    private val errorConverter: ErrorConverter
) : AppRepository {

    override suspend fun selectAllHabitsFromDB(): List<Habit> =

        habitsDao.selectAllHabits().map {
            habitMapper.habitDataToHabitDomain(it)
        }

    override suspend fun insertHabitIntoDB(habit: Habit) =
        habitsDao.insertHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun insertHabitsIntoDB(habits: List<Habit>) {
        habits.forEach { habit ->
            habitsDao.insertHabit(habitMapper.habitDomainToHabitData(habit))
        }
    }

    override suspend fun updateHabitInDB(habit: Habit) =
        habitsDao.updateHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun deleteHabitFromDB(habit: Habit) =
        habitsDao.deleteHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun deleteAllHabitsFromDB() =
        habitsDao.deleteAllHabits()

    override suspend fun putHabitIntoApi(habit: Habit): ResponseData.PutResponseData {
        val response = cloudRepository.putHabit(habitMapper.habitToServerHabit(habit))
        if (response.isSuccessful) {
            response.body()?.let { uidFromApi ->
                return ResponseData.PutResponseData(
                    errorMessage = null,
                    isSuccessful = true,
                    uid = uidFromApi
                )
            }
        } else {
            response.errorBody()?.let { errorBody ->
                return ResponseData.PutResponseData(
                    errorMessage = errorConverter.convertError(errorBody),
                    isSuccessful = false,
                    uid = null
                )
            }
        }

        return ResponseData.PutResponseData(
            errorMessage = "Unknown error",
            isSuccessful = false,
            uid = null
        )
    }


    override suspend fun getHabitsFromApi(): ResponseData.GetResponseData {
        val response = cloudRepository.getAllHabits()
        if (response.isSuccessful) {
            response.body()?.let { habitsFromServer ->
                return ResponseData.GetResponseData(
                    errorMessage = null,
                    isSuccessful = true,
                    habitsList = habitsFromServer.map {
                        habitMapper.serverHabitToHabit(it)
                    }
                )
            }
        } else {
            response.errorBody()?.let { errorBody ->
                return ResponseData.GetResponseData(
                    errorMessage = errorConverter.convertError(errorBody),
                    isSuccessful = false,
                    habitsList = null
                )
            }
        }

        return ResponseData.GetResponseData(
            errorMessage = "Unknown error",
            isSuccessful = false,
            habitsList = null
        )
    }


    override suspend fun postHabitDone(postDone: PostDone): ResponseData {
        val response = cloudRepository.postHabitDone(postDone)
        if (response.isSuccessful) {
            return ResponseData(
                errorMessage = null,
                isSuccessful = true,
            )
        } else {
            response.errorBody()?.let { errorBody ->
                return ResponseData(
                    errorMessage = errorConverter.convertError(errorBody),
                    isSuccessful = false
                )
            }
        }

        return ResponseData(
            errorMessage = "Unknown error",
            isSuccessful = false
        )
    }


    override suspend fun deleteHabitFromApi(uid: Uid): ResponseData {
        val response = cloudRepository.deleteHabit(uid)
        if (response.isSuccessful) {
            println("delete success")
            return ResponseData(
                errorMessage = null,
                isSuccessful = true,
            )
        } else {
            println("delete not success")
            response.errorBody()?.let { errorBody ->
                return ResponseData(
                    errorMessage = errorConverter.convertError(errorBody),
                    isSuccessful = false
                )
            }
        }
        return ResponseData(
            errorMessage = "Unknown error",
            isSuccessful = false
        )
    }


    override suspend fun postHabitsDone(postDones: List<PostDone>): ResponseData {
        postDones.forEach {
            val response = cloudRepository.postHabitDone(it)
            if (!response.isSuccessful) {
                response.errorBody()?.let { responseBody ->
                    return ResponseData(
                        errorMessage = errorConverter.convertError(responseBody),
                        isSuccessful = false
                    )
                }
            }
        }
        return ResponseData(
            errorMessage = null,
            isSuccessful = true,
        )
    }

}