package ru.romanoval.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable

@Entity
data class HabitRoom(
    var uid: String?,
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
) : Serializable {

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