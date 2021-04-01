package ru.romanoval.data.mapper

import ru.romanoval.data.model.Habit
import ru.romanoval.domain.model.restful.ServerHabit

class HabitMapper {

    fun habitDataToHabitDomain(habit: Habit) =
        ru.romanoval.domain.model.Habit(
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

    fun habitDomainToHabitData(habit: ru.romanoval.domain.model.Habit) =
        Habit(
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

    fun serverHabitToHabit(habit: ServerHabit) =
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