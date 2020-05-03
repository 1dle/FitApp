package com.undef.fitapp.ui.mapexc.previous

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.undef.fitapp.R

class GpsExerciseViewer : AppCompatActivity() {

    lateinit var mMapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps_exercise_viewer)

        mMapView =
            findViewById<View>(R.id.map2) as MapView
        mMapView.onCreate(savedInstanceState)

        mMapView.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            // For showing a move to my location button
            googleMap.isMyLocationEnabled = true
        }

    }

    /*override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }*/
}
