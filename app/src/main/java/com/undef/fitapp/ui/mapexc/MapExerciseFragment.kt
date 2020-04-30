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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            googleMap!!.isMyLocationEnabled = false


            //last location
            viewModel.myLocationProvider.getLastLocation().addOnSuccessListener { location : Location? ->
                if(location!=null){
                    val cameraPosition =
                        CameraPosition.Builder().target(
                            LatLng(location.latitude, location.longitude)
                        ).zoom(12f).build()
                    googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }

        val btnTrackStartStop = rootView.findViewById<Button>(R.id.btnTrackStartStop).apply{
            setOnClickListener {

                if(viewModel.myLocationProvider.status == TrackStatus.STOP){
                    //ha nincs még trackelve a cucc

                    viewModel.myLocationProvider.startLocationUpdates()

                    Log.d(MapExerciseFragment::class.java.name, "start")
                    btnTrackStartStop.setText("Stop")
                }else if(viewModel.myLocationProvider.status == TrackStatus.RUN){
                    viewModel.myLocationProvider.stopLocationUpdates()
                    Log.d(MapExerciseFragment::class.java.name, "stop")
                    btnTrackStartStop.setText("Start")
                }
            }
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

