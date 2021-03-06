package com.example.myhabits3.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.romanoval.data.mapper.HabitMapper
import ru.romanoval.data.source.database.AppDatabase
import ru.romanoval.data.source.database.HabitsDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(appDataBase: AppDatabase): HabitsDao {
        return appDataBase.habitsDao()
    }

    @Provides
    fun provideHabitMapper(): HabitMapper {
        return HabitMapper()
    }

}