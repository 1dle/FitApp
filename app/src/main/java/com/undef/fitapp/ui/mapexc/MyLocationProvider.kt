package com.undef.fitapp.ui.mapexc

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import android.location.Location as Location

enum class TrackStatus{
    RUN,
    STOP
}
class MyLocationProvider(val context: Context, private val locationCallback: MyLocationCallback){

    private val fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest

    var status = TrackStatus.STOP



    init{
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        locationRequest = LocationRequest.create().apply {
            interval = 1000 //1 sec
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        status = TrackStatus.RUN
    }
    fun stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
        status = TrackStatus.STOP
    }

    fun getLastLocation() : Task<Location?> {
        return fusedLocationClient.lastLocation
    }

}

abstract class MyLocationCallback(): LocationCallback(){

    abstract fun newLocation(location: Location?)

    final override fun onLocationResult(p0: LocationResult?) {
        if(p0!= null){
            p0.lastLocation?.let {
                newLocation(it)
            }
        }

    }
}
