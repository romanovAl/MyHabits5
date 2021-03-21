package com.example.myhabits3.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true)
    var bdId: Int?,
    var title: String,
    var description: String,
    var priority: Int,
    var type: Int,
    var count: Int,
    var frequency: Int,
    var color: Int,
    var date: Long,
    @TypeConverters(ListConverter::class)
    var doneDates: MutableList<Long>
) : Parcelable {

    class ListConverter {
        @TypeConverter
        fun fromHabits(habits: MutableList<Long>): String {
            return habits.joinToString()
        }

        @TypeConverter
        fun toHabits(data: String): MutableList<Long> {
            return if (data != "") {
                data.split(", ").map { it.toLong() }.toMutableList()
            } else {
                mutableListOf()
            }
        }
    }

}