package ru.romanoval.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.romanoval.data.model.HabitRoom

@Database(entities = [HabitRoom::class], version = AppDatabase.VERSION)
@TypeConverters(HabitRoom.ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitsDao(): HabitsDao

    companion object {
        const val DB_NAME = "habits.db"
        const val VERSION = 3
    }

}