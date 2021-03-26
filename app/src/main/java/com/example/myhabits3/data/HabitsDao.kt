package com.example.myhabits3.data

import androidx.room.*
import com.example.myhabits3.model.Habit

@Dao
interface HabitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * from Habit ORDER BY bdId DESC")
    suspend fun selectAllHabits(): List<Habit>

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM Habit")
    suspend fun deleteAllHabits()

    @Update
    suspend fun updateHabit(habit: Habit)

}