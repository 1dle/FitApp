package com.undef.fitapp.api.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.undef.fitapp.R
import com.undef.fitapp.custom.ItemType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GpsExercise(
    @SerializedName("UserID")
    var userID: Int, // 0
    @SerializedName("Type")
    var typeGps: String, // string
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
): FoodNMet(), Parcelable{
    override fun getTitle() = String.format("Gps Exercise: %s, %s", typeGps, start.split("T").let { it[0].replace("-",".")+". "+it[1] })

    fun getShortTitle() = String.format("%s, %s", typeGps, start.split("T").let { it[0].replace("-",".")+". "+it[1] })

    override fun getKcals() = String.format("%.2f kcals",-burned)

    override fun getIcon() =
        when(typeGps){
            "cycling" -> R.drawable.ic_cycling
            "run" -> R.drawable.ic_exercise
            else -> R.drawable.ic_walk //persze ha walk van akkor is, de else kötelező
        }


    override fun getDateOfAdd(): String? = start

    override val type: ItemType
        get() = ItemType.GPSEX
}