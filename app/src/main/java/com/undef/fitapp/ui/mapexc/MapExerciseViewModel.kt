package com.undef.fitapp.ui.mapexc

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MapExerciseViewModel(application: Application): AndroidViewModel(application) {

    var myLocationProvider =

        MyLocationProvider(application.applicationContext, object : MyLocationCallback(){

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

}
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}