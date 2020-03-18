package com.undef.fitapp.ui.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.undef.fitapp.R
import com.undef.fitapp.models.Exercise
import com.undef.fitapp.models.Healthy
import com.undef.fitapp.models.Meal
import com.undef.fitapp.ui.custom.MEListAdapter

class SearchMnEActivity : AppCompatActivity() {
    //Search Meal and Exercise Activity

    private lateinit var searchMnEActivity: SearchMnEActivity

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_mn_e)

        /*val tvConsumed: TextView = root.findViewById(R.id.tvConsumed)
        diaryViewModel.consumedText.observe(viewLifecycleOwner, Observer {
            tvConsumed.text = it
        })*/
        viewManager = LinearLayoutManager(this)
        viewAdapter = MEListAdapter(mutableListOf<Healthy>(
            Meal(1,"apple",100.0),
            Exercise(2,"Running",-320.1),
            Meal(3,"pear",102.0)
        ))

        recyclerView = findViewById<RecyclerView>(R.id.rvResultME).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }
}
