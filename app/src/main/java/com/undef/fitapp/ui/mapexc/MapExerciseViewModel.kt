package com.undef.fitapp.ui.mapexc

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

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
                    _trace.value!!.add(location)
                    prevLocation = currentLocation.value

                }

            }
        })
    var currentLocation: MutableLiveData<Location> = MutableLiveData()

    //trace of current exercise
    private var _trace =  MutableLiveData<MutableList<Location>>().apply {
        value = mutableListOf<Location>()
    }

    val trace : LiveData<MutableList<Location>> = _trace

}