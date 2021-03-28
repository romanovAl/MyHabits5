package com.example.myhabits3.repository

import com.example.myhabits3.data.HabitsDao
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.PostDone
import com.example.myhabits3.model.ServerHabit
import com.example.myhabits3.model.Uid
import com.example.myhabits3.restful.ApiService
import okhttp3.Call
import retrofit2.Response
import java.lang.Error

class Repository(
    private val habitsDao: HabitsDao,
    private val api: ApiService
) {

    //DB--------------------------------------------------------

    suspend fun insertHabitIntoDB(habit: Habit) {
        habitsDao.insertHabit(habit)
    }

    suspend fun insertHabitsIntoDB(habits: List<Habit>) {
        habits.forEach {
            habitsDao.insertHabit(it)
        }
    }

    suspend fun getHabitsFromDB(): List<Habit> {
        return habitsDao.selectAllHabits()
    }

    suspend fun deleteHabitFromDB(habit: Habit) {
        habitsDao.deleteHabit(habit)
    }

    suspend fun deleteAllHabitsFromDB() {
        habitsDao.deleteAllHabits()
    }

    suspend fun updateHabitInDB(habit: Habit) {
        habitsDao.updateHabit(habit)
    }

    //DB--------------------------------------------------------

    //API------------------------------------------------------

    suspend fun insertHabitIntoApi(habit: ServerHabit): Response<Uid> =
        api.putHabit(habit)


    suspend fun getHabitsFromApi(): Response<List<ServerHabit>> =
        api.getHabits()


    suspend fun postHabitDone(postDone: PostDone): Response<Unit> =
        api.postHabitDone(postDone)


    suspend fun deleteHabitFromApi(uid: Uid): Response<Unit> =
        api.deleteHabit(uid)

    //API------------------------------------------------------

}