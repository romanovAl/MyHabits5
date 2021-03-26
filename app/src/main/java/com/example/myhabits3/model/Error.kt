package com.example.myhabits3.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Error(
    var code : Int,
    var message : String
):Parcelable