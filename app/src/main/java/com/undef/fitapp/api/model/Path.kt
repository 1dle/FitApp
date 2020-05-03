package com.undef.fitapp.api.model


import com.google.gson.annotations.SerializedName

data class Path(
    @SerializedName("Lat")
    var lat: Double, // 0
    @SerializedName("Lng")
    var lng: Double // 0
)