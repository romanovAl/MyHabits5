package com.example.myhabits3.model

import java.io.Serializable

data class Habit  (
    var name: String, var description: String, var priority: String, var type:Boolean,
    var period:String, var color: Int, var times: Int?
) : Serializable