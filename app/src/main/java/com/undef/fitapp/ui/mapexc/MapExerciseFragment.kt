package com.undef.fitapp.ui.mapexc

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.undef.fitapp.R
import kotlinx.android.synthetic.main.fragment_mapexercise.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timer


class MapExerciseFragment : Fragment(){

    lateinit var viewModel : MapExerciseViewModel

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null

    //map drawing stuffs
    private lateinit var userPostionCircle: Circle
    private var _userPosCircleInitialized = false

    private lateinit var userRoutePolyline: Polyline
    private var _userRoutePolylineInitialized = false

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
            drawCurrentLocation(it)
        })
        viewModel.trace.observe(this, Observer {
            //ha frissül a trace lista, új elem lett hozzáadva
            if(viewModel.myLocationProvider.status == TrackStatus.RUN){
                Log.d("CURRLOCATION", "ujpoz")
                updateDrawedRoute(it)
            }

        })
        viewModel.elapsedSeconds.observe(this, Observer {
            rootView.findViewById<TextView>(R.id.tvTrackTimer).setText(viewModel.elapsedTime)
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
                    moveMapCam(
                        LatLng(location.latitude, location.longitude)
                    )
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
                    viewModel.startTimer()


                }else if(viewModel.myLocationProvider.status == TrackStatus.RUN){
                    viewModel.myLocationProvider.stopLocationUpdates()
                    Log.d(MapExerciseFragment::class.java.name, "stop")
                    btnTrackStartStop.setText("Start")

                    viewModel.stopTimer()
                }
            }
        }


        return rootView

    }
    fun updateDrawedRoute(locations: List<Location>){
        if(_userRoutePolylineInitialized){
            userRoutePolyline.remove()
        }

        val route = PolylineOptions()
        locations.forEach {
            route.add(LatLng(it.latitude, it.longitude))
        }

        userRoutePolyline = googleMap!!.addPolyline(route.color(Color.BLUE))

        _userRoutePolylineInitialized = true


    }

    fun drawCurrentLocation(location: Location){

        if(_userPosCircleInitialized){
            //levesszük az előzőt a mapről
            userPostionCircle.remove()
        }

        val circleOptions: CircleOptions = CircleOptions()
            .center(LatLng(location.latitude, location.longitude))
            .radius(200.0) // In meters
            .strokeWidth(3.0f)
            .fillColor(Color.CYAN)
        userPostionCircle = googleMap!!.addCircle(circleOptions)

        moveMapCam(
            LatLng(userPostionCircle.center.latitude, userPostionCircle.center.longitude)
        )
        _userPosCircleInitialized = true
    }

    fun moveMapCam(latlng: LatLng){
        val cameraPosition =
            CameraPosition.Builder().target(
                latlng
            ).zoom(12f).build()
        googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

