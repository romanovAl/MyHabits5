package com.example.myhabits3.model

import java.io.Serializable

data class Habit(
    var title: String,
    var description: String,
    var priority: Int,
    var type: Int,
    var count: Int,
    var frequency: Int,
    var color: Int,
    var date: Long,
    var doneDates: MutableList<Long>
) : Serializable