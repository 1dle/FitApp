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
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.getbase.floatingactionbutton.FloatingActionButton
import com.undef.fitapp.R
import com.undef.fitapp.ui.custom.MEListAdapter
import com.undef.fitapp.ui.custom.SearchMode
import kotlinx.android.synthetic.main.fragment_diary.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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

        diaryViewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            tvDiaryDate.text = diaryViewModel.selectedDateAsString()
        })


        recyclerView = root.findViewById(R.id.rvDiaryDaily)
        diaryViewModel.foodNMet.observe(viewLifecycleOwner, Observer { fnm ->
            viewManager = LinearLayoutManager(context)
            viewAdapter = MEListAdapter(fnm, SearchMode.NONE,this)
            recyclerView.layoutManager = viewManager
            recyclerView.adapter = viewAdapter
        })

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
            intent.putExtra("search_mode", SearchMode.MEAL)
            startActivity(intent)
            fab.collapse()
        }
        val fabAddExercise: FloatingActionButton = view.findViewById(R.id.fabAddExercise)
        fabAddExercise.setOnClickListener {
            val intent = Intent(activity, SearchMnEActivity::class.java)
            intent.putExtra("search_mode", SearchMode.EXERCISE)
            startActivity(intent)
            fab.collapse()
        }

        btnPrevDate.setOnClickListener {
            diaryViewModel.incrementDate(-1)
        }
        btnNextDate.setOnClickListener {
            diaryViewModel.incrementDate(1)
        }
        tvDiaryDate.setOnClickListener {
            MaterialDialog(view.context).show {

                datePicker(currentDate = diaryViewModel.selectedDate.value!!.toCalendar() ){ dialog, datetime ->
                    diaryViewModel.setDate(datetime.time)
                }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            diaryViewModel.getDailyData()
        }
/*
        if(activity!!.intent.extras != null)
            if(activity!!.intent.hasExtra("where"))
                if(activity!!.intent.getStringExtra("where") == "addmeal"){
                    //frissites
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.getDailyData()
                    }
                }*/
    }

    override fun onMEListItemClick(position: Int) {

    }


}
