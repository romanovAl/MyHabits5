package ru.romanoval.data.mapper

import ru.romanoval.data.model.HabitRoom
import ru.romanoval.data.model.ServerHabit
import ru.romanoval.domain.model.Habit

class HabitMapper {

    fun habitDataToHabitDomain(habitRoom: HabitRoom) =
        Habit(
            uid = habitRoom.uid,
            bdId = habitRoom.bdId,
            title = habitRoom.title,
            description = habitRoom.description,
            priority = habitRoom.priority,
            type = habitRoom.type,
            count = habitRoom.count,
            frequency = habitRoom.frequency,
            color = habitRoom.color,
            date = habitRoom.date,
            doneDates = habitRoom.doneDates
        )

    fun habitDomainToHabitData(habit: Habit) =
        HabitRoom(
            uid = habit.uid,
            bdId = habit.bdId,
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

    fun habitToServerHabit(habit: Habit) =
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

    fun serverHabitToHabit(habit : ServerHabit): Habit {
        println(habit)
        return Habit(
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
    )}
}