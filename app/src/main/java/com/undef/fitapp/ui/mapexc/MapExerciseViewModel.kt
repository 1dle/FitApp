package com.undef.fitapp.ui.mapexc

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

enum class TrackStatus{
    RUN,
    STOP
}
class MapExerciseViewModel(application: Application): AndroidViewModel(application) {

    var trackStatus = MutableLiveData<TrackStatus>().apply {
        value = TrackStatus.STOP
    }
    /**
     * Location related stuff
     */
    var myLocationProvider = MyLocationProvider(application.applicationContext, object : MyLocationCallback(){

            var prevLocation : Location? = null

            override fun newLocation(location: Location?) {
                location ?: return //ha null a location
                currentLocation.postValue(location)
                //csak akkor adom hozzá ha az új hely nincs benne már listában
                if(prevLocation == null || (prevLocation!!.latitude != location.latitude ||
                        prevLocation!!.longitude != location.longitude)){
                    //Log.d("CURRLOCATION", "uj location")

                    //ne rajzoljon egy vonalat amikor a kezdő helyre ugrik
                    if(prevLocation != null){
                        trace.value!!.add(location) //útvonalhoz hozzáadás
                        trace.notifyObserver() //observerek értesítése


                        currentSpeed.value = location.speed.toInt() //speed frissítése
                        allSpeeds.add(location.speed.toInt())


                        //távolság számolása
                        _distanceInKms.value = _distanceInKms.value!! + getDistanceFromLatLonInKm(LatLng(
                            prevLocation!!.latitude, prevLocation!!.longitude), LatLng(location.latitude, location.longitude))
                    }
                    prevLocation = currentLocation.value

                }


            }
        })
    var currentLocation: MutableLiveData<Location> = MutableLiveData()
    //trace of current exercise
    var trace =  MutableLiveData<MutableList<Location>>().apply {
        value = mutableListOf<Location>()
    }

    private val allSpeeds = mutableListOf<Int>()
    var currentSpeed = MutableLiveData<Int>().apply { value = 0 }

    val avgSpeed: String
        get() = String.format("%.2f", allSpeeds.sum().toDouble() / allSpeeds.size.toDouble())

    /**
     * Activity related stuff
     */
    var selectedActivityType = ActivityCalculator.ActivityType.NONE
    var currentBurned = 0.0


    /**
     * Distance measuring
     */
    private val _distanceInKms = MutableLiveData<Double>().apply { value = 0.0 }
    val distanceTraveled: String
        get() = String.format("%.2f",_distanceInKms.value)

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

    /**
     * Timer related stuff
     */
    var prevExerciseLengthInSeconds = 0;
    val elapsedSeconds = MutableLiveData<Int>().apply { value = 0 }
    val elapsedTime: String
        get() = String.format(
            "%02d:%02d:%02d",
            elapsedSeconds.value!! / 3600,
            (elapsedSeconds.value!! % 3600) / 60,
            elapsedSeconds.value!! % 60
        );

    val durationInMinutes: Double
        get() = prevExerciseLengthInSeconds.toDouble() / 60

    private var _timer: Timer? = null
    fun startTimer(){
        _timer = kotlin.concurrent.timer(period = 1000){

            CoroutineScope(Dispatchers.Main).launch {
                elapsedSeconds.value = elapsedSeconds.value!!+1}
            }

    }
    fun stopTimer(){
        _timer?.cancel()
        prevExerciseLengthInSeconds = elapsedSeconds.value!!
        elapsedSeconds.setValue(0)
    }

    fun reset() {
        //clear full trace list
        trace.value!!.clear()
        //remove allSpeeds what used for determine avgSpeed
        allSpeeds.clear()
        currentSpeed.value = 0
        currentBurned = 0.0
        _distanceInKms.value = 0.0
        prevExerciseLengthInSeconds = 0
        elapsedSeconds.value = 0

    }

}
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}