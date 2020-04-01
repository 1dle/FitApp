package com.undef.fitapp.ui.diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getbase.floatingactionbutton.FloatingActionButton
import com.undef.fitapp.R
import com.undef.fitapp.models.FoodNMet
import com.undef.fitapp.ui.custom.MEListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryFragment : Fragment(), MEListAdapter.OnMEListItemClickListener{

    private lateinit var diaryViewModel: DiaryViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*return inflater.inflate(R.layout.fragment_diary, container, false)*/
        diaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_diary, container, false)

        val liveDailyStats = mutableListOf<Pair<TextView, LiveData<String>>>(
            // Pair ( UI element name , property name in ViewModel )
            Pair(root.findViewById(R.id.tvConsumed), diaryViewModel.consumedText),
            Pair(root.findViewById(R.id.tvBurned), diaryViewModel.burnedText),
            Pair(root.findViewById(R.id.tvRemaining), diaryViewModel.remainingText)
        )

        liveDailyStats.forEach { pit ->
            pit.second.observe(viewLifecycleOwner, Observer {
                pit.first.text = it
            })
        }


        recyclerView = root.findViewById(R.id.rvDiaryDaily)
        diaryViewModel.foodNMet.observe(viewLifecycleOwner, Observer { fnm ->
            viewManager = LinearLayoutManager(context)
            viewAdapter = MEListAdapter(fnm,this)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })

        //test live data
        /*
        liveDailyStats[0].first.setOnClickListener {
            diaryViewModel.changeConsumedText();
        }*/

        /*
        // the forEach is same as these lines but shorter
        // Consumed kcals from viewmodel
        val tvConsumed: TextView = root.findViewById(R.id.tvConsumed)
        diaryViewModel.consumedText.observe(viewLifecycleOwner, Observer {
            tvConsumed.text = it
        })
        //Burned kcals
        val tvBurned: TextView = root.findViewById(R.id.tvBurned)
        diaryViewModel.burnedText.observe(viewLifecycleOwner, Observer {
            tvBurned.text = it
        })
        //Remaining kcals
        val tvRemaining: TextView = root.findViewById(R.id.tvRemaining)
        diaryViewModel.remainingText.observe(viewLifecycleOwner, Observer {
            tvRemaining.text = it
        })*/

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            diaryViewModel.getDailyData()
        }

        val fabAddMeal: FloatingActionButton = view.findViewById(R.id.fabAddMeal)
        fabAddMeal.setOnClickListener {
            //Toast.makeText(this,"addMeal", Toast.LENGTH_SHORT).show();
            val intent = Intent(activity, SearchMnEActivity::class.java)
            startActivity(intent)
        }
        val fabAddExercise: FloatingActionButton = view.findViewById(R.id.fabAddExercise)
        fabAddExercise.setOnClickListener {
            //Toast.makeText(this,"addExercise", Toast.LENGTH_SHORT).show();
        }
/*
        viewManager = LinearLayoutManager(context)
        viewAdapter = MEListAdapter(diaryViewModel.foodNMet.value!!)

        recyclerView = view.findViewById<RecyclerView>(R.id.rvDiaryDaily).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }*/


    }

    override fun onMEListItemClick(position: Int) {

    }
}
