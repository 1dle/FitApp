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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.materialspinner.MaterialSpinner
import com.undef.fitapp.R
import com.undef.fitapp.api.repositories.UserDataRepository
import kotlinx.android.synthetic.main.fragment_mapexercise.*
import kotlinx.coroutines.channels.ActorScope
import java.util.*

class MapExerciseFragment : Fragment(){

    lateinit var viewModel : MapExerciseViewModel

    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null

    //map drawing stuffs
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

        initActivitySpinner(rootView.findViewById(R.id.spTrackActivityChooser) as MaterialSpinner)

        viewModel = ViewModelProviders.of(this).get(MapExerciseViewModel::class.java)

        viewModel.currentLocation.observe(this, Observer {
            Log.d("CURRLOCATION", "lat: ${it.latitude} lng: ${it.longitude}")
            //drawCurrentLocation(it)
        })
        viewModel.trace.observe(this, Observer {
            //ha frissül a trace lista, új elem lett hozzáadva
            if(viewModel.trackStatus.value == TrackStatus.RUN){
                //Log.d("CURRLOCATION", "ujpoz")
                updateDrawedRoute(it)
                //set speed text
                rootView.findViewById<TextView>(R.id.tvTrackSpeed).text = "Speed:\n${viewModel.currentSpeed.value} m/s"
                //set distance text
                rootView.findViewById<TextView>(R.id.tvTrackDistance).text = "Distance:\n${viewModel.distanceTraveled} km"

            }

        })
        viewModel.elapsedSeconds.observe(this, Observer {
            rootView.findViewById<TextView>(R.id.tvTrackTimer).setText(viewModel.elapsedTime)
        })
        viewModel.trackStatus.observe(this, Observer {
            if(it == TrackStatus.STOP){
                //reset UI texts on stop
                //rootView.findViewById<TextView>(R.id.tvTrackTimer).setText("00:00:00")
                rootView.findViewById<TextView>(R.id.tvTrackDistance).text = "Distance:\n0 km"
                rootView.findViewById<TextView>(R.id.tvTrackSpeed).text = "Speed:\n0 m/s"
            }

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
                if(viewModel.trackStatus.value == TrackStatus.STOP){
                    viewModel.trackStatus.value = TrackStatus.RUN
                    //ha nincs még trackelve a cucc
                    viewModel.myLocationProvider.startLocationUpdates()
                    //Log.d(MapExerciseFragment::class.java.name, "start")
                    btnTrackStartStop.setText("Stop")
                    viewModel.startTimer()




                }else if(viewModel.trackStatus.value == TrackStatus.RUN){
                    viewModel.trackStatus.value = TrackStatus.STOP

                    viewModel.myLocationProvider.stopLocationUpdates()
                    //Log.d(MapExerciseFragment::class.java.name, "stop")
                    btnTrackStartStop.setText("Start")
                    viewModel.stopTimer()

                    showActivityResults() //dialog



                }
            }
        }


        return rootView
    }

    fun resetEverything(){
        //map, timer, walked distance, whole trace, elpased seconds, walked distances

        //view model stuffs reset
        viewModel.reset();

        //1. clear map
        userRoutePolyline.remove();
        googleMap!!.clear()
    }

    fun showActivityResults(){
        MaterialDialog(context!!).show{
            noAutoDismiss()
            customView(R.layout.dialog_gps_exercise_result)

            //set texts
            findViewById<TextView>(R.id.tvERactivityType).replace(viewModel.selectedActivityType.name.toLowerCase(Locale.ROOT))

            findViewById<TextView>(R.id.tvERdistance).replace(viewModel.distanceTraveled)
            findViewById<TextView>(R.id.tvERduration).replace(String.format("%.2f minutes", viewModel.durationInMinutes))
            findViewById<TextView>(R.id.tvERavgSpeed).replace(viewModel.avgSpeed)

            viewModel.currentBurned = ActivityCalculator.burnedKcals(viewModel.selectedActivityType, viewModel.durationInMinutes, UserDataRepository.loggedUser.weight)

            findViewById<TextView>(R.id.tvERburned).replace(String.format("%.2f", viewModel.currentBurned))

            //DISCARD BUTTON
            findViewById<TextView>(R.id.btnERdiscard).setOnClickListener {
                resetEverything();
                dismiss();
            }

            //SAVE TO DIARY BUTTON
            findViewById<TextView>(R.id.btnERaddtoDiary).setOnClickListener {
                //küldés a szerverre



                //reset
                resetEverything();
                dismiss();
            }

        }
    }

    fun TextView.replace(value: String){
        this.text = this.text.toString().replace("@", value)
    }

    fun initActivitySpinner(spinner : MaterialSpinner){

        spinner.setItems(
            "Walk","Run","Cycling"
        )
        spinner.setOnItemSelectedListener { view, position, id, item ->
            viewModel.selectedActivityType = when(id){
                0L -> ActivityCalculator.ActivityType.WALK
                1L -> ActivityCalculator.ActivityType.RUN
                2L -> ActivityCalculator.ActivityType.CYCLING
                else -> ActivityCalculator.ActivityType.NONE
            }
            //Snackbar.make(view, viewModel.selectedActivityType.name, Snackbar.LENGTH_SHORT).show()
        }
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

