package com.undef.fitapp.ui.mapexc

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapExerciseViewModel: ViewModel() {
    private var currentLocation: MutableLiveData<Location> = MutableLiveData()

    //trace of current exercise
    private var trace =  MutableLiveData<MutableList<Location>>().apply {
        value = mutableListOf<Location>()
    }

    fun addLocToTrace(location: Location){
        trace.value!!.add(location)
    }
}