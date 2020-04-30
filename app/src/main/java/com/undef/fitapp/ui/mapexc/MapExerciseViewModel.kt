package com.undef.fitapp.ui.mapexc

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer

class MapExerciseViewModel(application: Application): AndroidViewModel(application) {
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
                        trace.value!!.add(location)
                        trace.notifyObserver()
                        currentSpeed.value = location.speed.toInt()
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
    var currentSpeed = MutableLiveData<Int>().apply { value = 0 }

    /**
     * Timer related stuff
     */
    val elapsedSeconds = MutableLiveData<Int>().apply { value = 0 }
    val elapsedTime: String
        get() = String.format(
            "%02d:%02d:%02d",
            elapsedSeconds.value!! / 3600,
            (elapsedSeconds.value!! % 3600) / 60,
            elapsedSeconds.value!! % 60
        );
    private var _timer: Timer? = null
    fun startTimer(){
        _timer = kotlin.concurrent.timer(period = 1000){

            CoroutineScope(Dispatchers.Main).launch {
                elapsedSeconds.value = elapsedSeconds.value!!+1}
            }

    }
    fun stopTimer(){
        _timer?.cancel()
        elapsedSeconds.setValue(0)
    }

}
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}