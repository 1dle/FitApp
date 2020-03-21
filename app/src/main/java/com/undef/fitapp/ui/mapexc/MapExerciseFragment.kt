package com.undef.fitapp.ui.mapexc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.undef.fitapp.R


class MapExerciseFragment : Fragment() {
    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
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

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
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

