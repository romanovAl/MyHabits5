package com.example.myhabits3.model

import android.os.Parcelable
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Type

@Parcelize
data class ServerHabit(
    var uid : String?,
    var title: String,
    var description: String,
    var priority: Int,
    var type: Int,
    var count: Int,
    var frequency: Int,
    var color: Int,
    var date: Long,
    var doneDates: MutableList<Long>
) : Parcelable {

    data class DoneDates(
        @SerializedName("done_dates")
        var done_dates: MutableList<Long>
    )

    class HabitJsonDeserializer : JsonDeserializer<ServerHabit> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ServerHabit {
            val list = GsonBuilder().create()
                .fromJson<DoneDates>(
                    json.asJsonObject,
                    DoneDates::class.java
                ).done_dates
            return ServerHabit(
                uid = json.asJsonObject.get("uid").asString,
                title = json.asJsonObject.get("title").asString,
                description = json.asJsonObject.get("description").asString,
                priority = json.asJsonObject.get("priority").asInt,
                type = json.asJsonObject.get("type").asInt,
                count = json.asJsonObject.get("count").asInt,
                frequency = json.asJsonObject.get("frequency").asInt,
                color = json.asJsonObject.get("color").asInt,
                date = json.asJsonObject.get("date").asLong,
                doneDates = list.toMutableList()
            )

        }
    }

}
