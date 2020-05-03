package com.undef.fitapp.api.model


import com.google.gson.annotations.SerializedName

data class GpsExercise(
    @SerializedName("UserID")
    var userID: Int, // 0
    @SerializedName("Type")
    var type: String, // string
    @SerializedName("Start")
    var start: String, // 2020-05-03T18:12:45.665Z
    @SerializedName("Duration")
    var duration: Double, // 0
    @SerializedName("AvgSpeed")
    var avgSpeed: Double, // 0
    @SerializedName("Burned")
    var burned: Double, // 0
    @SerializedName("Path")
    var path: List<Path>
)