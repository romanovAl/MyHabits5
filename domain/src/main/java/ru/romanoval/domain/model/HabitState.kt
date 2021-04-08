package ru.romanoval.domain.model

data class HabitState(
    val doneState: Int,
    val count: Int
) {

    companion object {
        const val doneNotEnough = 1
        const val doneJustEnough = 2
        const val doneMoreThanNeeded = 3
    }


}