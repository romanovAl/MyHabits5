package ru.romanoval.data.source.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.romanoval.data.model.Habit

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