package com.undef.fitapp.ui.mapexc

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MapExerciseViewModel(application: Application): AndroidViewModel(application) {

    var myLocationProvider =

        MyLocationProvider(application.applicationContext, object : MyLocationCallback(){
            override fun newLocation(location: Location?) {
                currentLocation.postValue(location)
            }
        })



    var currentLocation: MutableLiveData<Location> = MutableLiveData()

    //trace of current exercise
    private var trace =  MutableLiveData<MutableList<Location>>().apply {
        value = mutableListOf<Location>()
    }

    fun addLocToTrace(location: Location){
        trace.value!!.add(location)
    }
    fun updateLocation(location: Location?) {
        currentLocation.value = location
    }
}