package com.undef.fitapp.api.model


import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
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

    fun getDurationHMS(): String {
        val secs = (duration * 60).toInt()
        return String.format(
            "%02d:%02d:%02d",
            secs / 3600,
            (secs % 3600) / 60,
            secs % 60
        )
    }

    fun getDistance(): String{
        var traveledInKms = 0.0;
        path.forEachIndexed { i, p ->
            if(i != path.size-1){
                traveledInKms+= getDistanceFromLatLonInKm(
                    LatLng(
                        p.lat, p.lng
                    ),
                    LatLng(
                        path[i+1].lat,
                        path[i+1].lng
                    )
                )
            }
        }
        return "%.2f km".format(traveledInKms)
    }

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
fun getDistanceFromLatLonInKm(latlng1: LatLng, latlng2: LatLng) : Double {
    val r = 6371; // Radius of the earth in km
    val dLat = deg2rad(latlng2.latitude-latlng1.latitude);  // deg2rad below
    val dLon = deg2rad(latlng2.longitude-latlng2.longitude);
    val a =
        Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(deg2rad(latlng1.latitude)) * Math.cos(deg2rad(latlng2.latitude)) *
                Math.sin(dLon/2) * Math.sin(dLon/2)
    ;
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    val d = r * c; // Distance in km
    return d;
}
fun deg2rad(deg: Double) = deg * (Math.PI/180)