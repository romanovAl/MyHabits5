package com.example.myhabits3.repository

import com.example.myhabits3.data.HabitsDao
import com.example.myhabits3.model.Habit
import com.example.myhabits3.model.PostDone
import com.example.myhabits3.model.ServerHabit
import com.example.myhabits3.model.Uid
import com.example.myhabits3.restful.ApiService

class Repository(
    private val habitsDao: HabitsDao,
    private val api: ApiService
) {

    suspend fun insertHabitIntoDB(habit: Habit) {
        habitsDao.insertHabit(habit)
    }

    suspend fun insertHabitsIntoDB(habits: List<Habit>){
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

    suspend fun deleteAllHabitsFromDB(){
        habitsDao.deleteAllHabits()
    }

    suspend fun updateHabitInDB(habit: Habit) {
        habitsDao.updateHabit(habit)
    }

    suspend fun insertHabitsIntoApi(habits: List<ServerHabit>) =
        habits.forEach {
            it.uid = null
            api.putHabit(
                it
            )
        }

    suspend fun insertHabitIntoApi(habit: ServerHabit) =
        api.putHabit(habit)


    suspend fun getHabitsFromApi() =
        api.getHabits()

    suspend fun postHabitsDone(habitsFromApi: List<ServerHabit>, habitsFromDb: List<ServerHabit>) {
        val habits = habitsFromDb.toMutableList()
        for (i in 0 until habits.size) {
            habits[i].uid = habitsFromApi[i].uid
        }

        habits.forEach { habit ->
            habit.doneDates.forEach {
                habit.uid?.let { uid ->
                    val postDone = PostDone(uid, it)
                    api.postHabitDone(
                        postDone
                    )
                }

            }
        }
    }

    suspend fun deleteHabitsFromApi(habits: List<ServerHabit>) =
        habits.forEach {
            it.uid?.let { uid ->
                api.deleteHabit(
                    Uid(uid)
                )
            }
        }

    suspend fun deleteHabitFromApi(habit: ServerHabit) =
        habit.uid?.let {
            api.deleteHabit(
                Uid(it)
            )
        }
}