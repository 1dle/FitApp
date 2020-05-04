package com.undef.fitapp.api.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Path(
    @SerializedName("Lat")
    var lat: Double, // 0
    @SerializedName("Lng")
    var lng: Double // 0
): Parcelable