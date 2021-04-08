package ru.romanoval.data.model


import com.google.gson.annotations.SerializedName

data class ServerHabit(
    @SerializedName("uid")
    var uid: String?,
    @SerializedName("title")
    var title: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("priority")
    var priority: Int,
    @SerializedName("type")
    var type: Int,
    @SerializedName("count")
    var count: Int,
    @SerializedName("frequency")
    var frequency: Int,
    @SerializedName("color")
    var color: Int,
    @SerializedName("date")
    var date: Long,
    @SerializedName("done_dates")
    var doneDates: List<Long>
)