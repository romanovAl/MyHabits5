package ru.romanoval.data.source.database

import androidx.room.*
import ru.romanoval.data.model.HabitRoom

@Dao
interface HabitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habitRoom: HabitRoom)

    @Query("SELECT * from HabitRoom ORDER BY bdId DESC")
    suspend fun selectAllHabits(): List<HabitRoom>

    @Delete
    suspend fun deleteHabit(habitRoom: HabitRoom)

    @Query("DELETE FROM HabitRoom")
    suspend fun deleteAllHabits()

    @Update
    suspend fun updateHabit(habitRoom: HabitRoom)

}