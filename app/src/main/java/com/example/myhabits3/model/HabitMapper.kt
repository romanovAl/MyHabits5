package com.example.myhabits3.model

class HabitMapper {

    fun habitToServerHabit(habit : Habit) =
        ServerHabit(
            uid = null,
            title = habit.title,
            description = habit.description,
            priority = habit.priority,
            type = habit.type,
            count = habit.count,
            frequency = habit.frequency,
            color = habit.color,
            date = habit.date,
            doneDates = habit.doneDates
        )

    fun serverHabitToHabit(habit : ServerHabit)=
        Habit(
            uid = habit.uid,
            bdId = null,
            title = habit.title,
            description = habit.description,
            priority = habit.priority,
            type = habit.type,
            count = habit.count,
            frequency = habit.frequency,
            color = habit.color,
            date = habit.date,
            doneDates = habit.doneDates.toMutableList()
        )

}