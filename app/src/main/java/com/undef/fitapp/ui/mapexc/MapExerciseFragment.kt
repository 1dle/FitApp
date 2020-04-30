package com.undef.fitapp.ui.mapexc

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.undef.fitapp.R
import kotlinx.android.synthetic.main.fragment_mapexercise.*

class MapExerciseFragment : Fragment(){

    lateinit var viewModel : MapExerciseViewModel

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null

    private var status = MapExerciseViewModel.TrackStatus.STOP

    //location updates
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        locationRequest = LocationRequest.create().apply {
            interval = 1000 //1 sec
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                locationResult?.lastLocation?.let {
                    //Log.e("changed the location", "values" + it.longitude + " " + it.latitude)
                    it.let { it -> viewModel.updateLocation(it) }
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =
            inflater.inflate(R.layout.fragment_mapexercise, container, false)
        mMapView =
            rootView.findViewById<View>(R.id.mapView) as MapView
        mMapView!!.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MapExerciseViewModel::class.java)

        viewModel.currentLocation.observe(this, Observer {
            Log.d("CURRLOCATION", "lat: ${it.latitude} lng: ${it.longitude}")
        })


        mMapView!!.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMapView!!.getMapAsync { mMap ->
            googleMap = mMap
            // For showing a move to my location button
            googleMap!!.isMyLocationEnabled = true
            // For dropping a marker at a point on the Map
            val sydney = LatLng(-34.0, 151.0)
            googleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"))
            // For zooming automatically to the location of the marker
            val cameraPosition =
                CameraPosition.Builder().target(sydney).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        val btnTrackStartStop = rootView.findViewById<Button>(R.id.btnTrackStartStop).apply{
            setOnClickListener {

                if(status == MapExerciseViewModel.TrackStatus.STOP){
                    startTracking()
                    Log.d(MapExerciseFragment::class.java.name, "start")
                    btnTrackStartStop.setText("Stop")
                }else if(status == MapExerciseViewModel.TrackStatus.RUN){
                    stopTracking()
                    Log.d(MapExerciseFragment::class.java.name, "stop")
                    btnTrackStartStop.setText("Start")
                }
            }
        }


        return rootView
    }

    fun stopTracking(){
        status = MapExerciseViewModel.TrackStatus.STOP
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    fun startTracking(){
        status = MapExerciseViewModel.TrackStatus.RUN

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

    }
    /*
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if(location!=null){
                Log.d(MapExerciseFragment::class.java.name, "last known location: Lat:${location.latitude} Lng:${location.longitude}")
            }

        }
    */


    override fun onResume() {
        super.onResume()
        //googleApiClient?.connect()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        //googleApiClient?.isConnected?.let { if (it) googleApiClient?.disconnect() }
        mMapView!!.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

}

