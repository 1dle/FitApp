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

class MapExerciseFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    lateinit var viewModel : MapExerciseViewModel

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null

    //location updates
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectToGoogleApiClient()
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

        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!);

        /*locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for(location in locationResult.locations){
                    //todo
                }
            }
        }*/

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
        return rootView
    }
    private fun connectToGoogleApiClient() {
        googleApiClient = activity?.let {
            GoogleApiClient.Builder(it)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000)
            .setFastestInterval(1000)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("CURRLOCATION", "onConnectionFailed")
    }

    override fun onConnected(bundle: Bundle?) {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity!!, permissions, 1000)
        } else {
            val location = LocationServices.getFusedLocationProviderClient(activity!!)
            location.requestLocationUpdates(locationRequest,object : LocationCallback() {
                override fun onLocationAvailability(p0: LocationAvailability?) {

                }
                override fun onLocationResult(p0: LocationResult?) {
                    p0?.lastLocation?.let {
                        Log.e("changed the location", "values" + it.longitude + " " + it.latitude)
                        it.let { it -> viewModel.updateLocation(it) }
                    }
                }

            },null)
            location.lastLocation.addOnSuccessListener {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectToGoogleApiClient()
                } else {
                    Toast.makeText(activity, "Cannot get Location Updates", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }


    override fun onResume() {
        super.onResume()
        googleApiClient?.connect()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        googleApiClient?.isConnected?.let { if (it) googleApiClient?.disconnect() }
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

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onLocationChanged(p0: Location?) {
        p0?.let { viewModel.updateLocation(p0) }
    }

}

