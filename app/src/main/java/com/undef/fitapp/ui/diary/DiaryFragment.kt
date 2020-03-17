package com.undef.fitapp.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.undef.fitapp.R
import kotlinx.android.synthetic.main.fragment_diary.*

class DiaryFragment : Fragment() {

    private lateinit var diaryViewModel: DiaryViewModel

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
}
