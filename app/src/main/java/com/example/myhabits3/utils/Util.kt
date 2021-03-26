package com.example.myhabits3.utils

import android.graphics.Color

class Util {

    companion object {

        val intColors = mutableMapOf(
            Pair(0, parse("#FFCBBB")),
            Pair(1, parse("#FFE8BB")),
            Pair(2, parse("#FFFFBB")),
            Pair(3, parse("#E9FFBB")),
            Pair(4, parse("#D1FFBB")),
            Pair(5, parse("#BBFFBC")),
            Pair(6, parse("#BBFFDA")),
            Pair(7, parse("#BBFFF3")),
            Pair(8, parse("#BBF4FF")),
            Pair(9, parse("#BBDAFF")),
            Pair(10, parse("#BBBFFF")),
            Pair(11, parse("#D1BBFF")),
            Pair(12, parse("#EABBFF")),
            Pair(13, parse("#FFB9FF")),
            Pair(14, parse("#FFBBE6")),
            Pair(15, parse("#FFBBCA")),
            Pair(16, parse("#9fa8da"))
        )

        private fun parse(stringColor: String): Int = Color.parseColor(stringColor)

        fun getColorNumberByColor(color: Int): Int {
            intColors.keys.forEach {
                if (intColors[it] == color) return it
            }

            return 16
        }

    }


}