package com.undef.fitapp.ui.createprofile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R
import com.undef.fitapp.api.repositories.MyCalendar
import com.undef.fitapp.api.repositories.MyCalendar.dateToString
import com.undef.fitapp.api.repositories.MyCalendar.getCurrentDate
import com.undef.fitapp.api.repositories.MyCalendar.today
import com.undef.fitapp.api.repositories.UserDataRepository
import kotlinx.android.synthetic.main.fragment_goal.*


class GoalFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_goal, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewPager : ViewPager = activity!!.findViewById(R.id.viewPagerCP)

        rgGoal.setOnCheckedChangeListener { _, i ->
            UserDataRepository.preRegUserData.goal = when(i){
                R.id.rbGoalLose -> -200
                R.id.rbGoalStay -> 0
                R.id.rbGoalGain -> 200
                else -> 0
            }
        }


        btnGDone.setOnClickListener{
            /*
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            */
            //teszthez
            if(rgGoal.checkedRadioButtonId != -1){
                UserDataRepository.preRegUserData.registerDate = dateToString(getCurrentDate())

                //todo: post registration to server, get the id and redirect to homeActvitiy

            }else{
                Toast.makeText(context, "Select a goal!", Toast.LENGTH_SHORT).show()
            }


        }


    }
}