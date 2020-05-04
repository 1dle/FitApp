package com.undef.fitapp.ui.mapexc.previous

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.undef.fitapp.R
import com.undef.fitapp.api.model.GpsExercise
import com.undef.fitapp.api.service.ConnectionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse


class PreviousGpsExercises : AppCompatActivity() {

    class GELAdapter(private val myDataset: List<GpsExercise>) :
        RecyclerView.Adapter<GELAdapter.MyViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): GELAdapter.MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_gpsexer, parent, false))
        }

        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            val gpse = myDataset.get(position)

            holder.view.apply{
                findViewById<ImageView>(R.id.ivGELtype).setImageResource(
                    gpse.getIcon()
                )

                findViewById<TextView>(R.id.tvGELburned).text = String.format("%.2f kcals", gpse.burned)
                findViewById<TextView>(R.id.tvGELshort).text = "%s\nduration: %s\ndistance: %s".format(gpse.getShortTitle(),gpse.getDurationHMS(),gpse.getDistance())

                //setup google maps
                lateinit var googleMap: GoogleMap
                val mMapView: MapView = findViewById<View>(R.id.map3) as MapView
                    mMapView.onCreate(null)
                    mMapView.onResume() // needed to get the map to display immediately
                    try {
                        MapsInitializer.initialize(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mMapView.getMapAsync { mMap ->
                        googleMap = mMap
                        // For showing a move to my location button
                        googleMap.isMyLocationEnabled = false
                        googleMap.uiSettings.setAllGesturesEnabled(false)
                        //googleMap.uiSettings.isScrollGesturesEnabled = true

                        //add first and last position
                        val firstPos =LatLng(
                            gpse.path.first().lat, gpse.path.first().lng
                        )
                        val lastPos = LatLng(
                            gpse.path.last().lat, gpse.path.last().lng
                        )
                        googleMap.addMarker(
                            MarkerOptions().position(firstPos).title("Starting position").icon(
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        googleMap.addMarker(
                            MarkerOptions().position(lastPos).title("Last position")
                        )
                        //googleMap.moveMapCam(firstPos)

                        val builder = LatLngBounds.Builder()
                        builder.include(firstPos)
                        builder.include(lastPos)

                        val bounds: LatLngBounds = builder.build()
                        val padding = 100 // offset from edges of the map in pixels

                        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                        googleMap.animateCamera(cu);

                        //draw whole route
                        val route = PolylineOptions()
                        gpse.path.forEach {
                            route.add(LatLng(it.lat, it.lng))
                        }
                        googleMap.addPolyline(route.color(Color.BLUE))
                    }

                setOnLongClickListener {
                    //context.startActivity( Intent(context, GpsExerciseViewer::class.java))
                    popupMenu {
                        dropdownGravity = Gravity.RIGHT
                        dropDownHorizontalOffset = -10
                        dropDownVerticalOffset = 10
                        section {
                            item {
                                label = "Delete"
                                icon = R.drawable.ic_delete
                                labelColor = Color.parseColor("#DD000A")
                                callback = {
                                    MaterialDialog(context!!).show {
                                        title (text = "Delete GPS exercise")
                                        message(text = "Delete \"${gpse.getShortTitle()}\"\nAre you sure?")
                                        positiveButton(text = "Yes"){
                                            CoroutineScope(Dispatchers.IO).launch {
                                                val resp = ConnectionData.service.deleteGpsExercise(gpse.userID).awaitResponse()
                                                if(resp.isSuccessful){
                                                    withContext(Dispatchers.Main){
                                                        Toast.makeText(context, "Gps exercise successfully deleted", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }

                                                }else{
                                                    withContext(Dispatchers.Main){
                                                        Toast.makeText(context, "Failed to delete GPS exercise", Toast.LENGTH_SHORT)
                                                            .show()
                                                    }

                                                }
                                            }
                                        }
                                        negativeButton(text = "No") { dismiss() }
                                    }
                                }
                            }
                        }
                    }.show(context!!, it.findViewById<TextView>(R.id.tvGELburned))
                    true
                }


            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }


    lateinit var viewModel: PGEViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_gps_exercises)

        viewModel = ViewModelProviders.of(this).get(PGEViewModel::class.java)


        recyclerView = findViewById<RecyclerView>(R.id.rvGEL)
        viewModel.exercises.observe(this, Observer {
            viewManager = LinearLayoutManager(applicationContext)
            viewAdapter = GELAdapter(it)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })


        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getExercises()
        }

    }
}




