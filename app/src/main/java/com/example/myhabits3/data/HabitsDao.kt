package com.example.myhabits3.data

import androidx.room.*
import com.example.myhabits3.model.Habit

@Dao
interface HabitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * from Habit ORDER BY bdId DESC")
    suspend fun selectAllHabits(): List<Habit>

//    @Query("SELECT * from Habit WHERE type = 0 ORDER BY bdId DESC")
//    fun selectBadHabits(): List<Habit>
//
//    @Query("SELECT * FROM Habit WHERE type = 1 ORDER BY bdId DESC")
//    fun selectGoodHabits(): List<Habit>

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

}