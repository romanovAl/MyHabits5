package com.example.myhabits3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myhabits3.model.Habit

@Database(entities = [Habit::class], version = AppDatabase.VERSION)
@TypeConverters(Habit.ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitsDao(): HabitsDao

    companion object {
        private const val DB_NAME = "habits.db"
        const val VERSION: Int = 1

        private var INSTANCE: AppDatabase? = null

        fun getHabitsDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DB_NAME
                        )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}
