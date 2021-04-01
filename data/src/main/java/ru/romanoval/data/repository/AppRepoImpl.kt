package ru.romanoval.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import repository.AppRepository
import retrofit2.Response
import ru.romanoval.data.mapper.HabitMapper
import ru.romanoval.data.source.cloud.BaseCloudRepository
import ru.romanoval.data.source.database.HabitsDao
import ru.romanoval.domain.model.Habit
import ru.romanoval.domain.model.restful.PostDone
import ru.romanoval.domain.model.restful.ServerHabit
import ru.romanoval.domain.model.restful.Uid
import javax.inject.Inject

class AppRepoImpl @Inject constructor(
    private val cloudRepository: BaseCloudRepository,
    private val habitsDao: HabitsDao,
    private val habitMapper: HabitMapper
) : AppRepository {

    override suspend fun selectAllHabits(): List<Habit> =

        habitsDao.selectAllHabits().map {
            habitMapper.habitDataToHabitDomain(it)
        }

    override suspend fun insertHabitIntoDB(habit: Habit) =
        habitsDao.insertHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun updateHabitInDB(habit: Habit) =
        habitsDao.updateHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun deleteHabitFromDB(habit: Habit) =
        habitsDao.deleteHabit(habitMapper.habitDomainToHabitData(habit))

    override suspend fun deleteAllHabitsFromDB() =
        habitsDao.deleteAllHabits()

    override suspend fun insertHabitIntoApi(habit: ServerHabit): Response<Uid> =
        cloudRepository.putHabit(habit)

    override suspend fun getHabitsFromApi(): Response<List<ServerHabit>> =
        cloudRepository.getAllHabits()


    override suspend fun postHabitDone(postDone: PostDone): Response<Unit> =
        cloudRepository.postHabitDone(postDone)


    override suspend fun deleteHabitFromApi(uid: Uid): Response<Unit> =
        cloudRepository.deleteHabit(uid)

}