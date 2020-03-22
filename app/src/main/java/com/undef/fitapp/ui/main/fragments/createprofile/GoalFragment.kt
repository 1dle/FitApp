package com.undef.fitapp.ui.main.fragments.createprofile

//import android.support.v4.app.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.HomeActivity
import com.undef.fitapp.R
import com.undef.fitapp.repositories.LoggedUserRepository
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
            LoggedUserRepository.instance.preRegUserData.goal = when(i){
                R.id.rbGoalLose -> "lose"
                R.id.rbGoalStay -> "stay"
                R.id.rbGoalGain -> "gain"
                else -> "not selected"
            }
        }


        btnGDone.setOnClickListener{
            /*
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            */
            //teszthez
            LoggedUserRepository.instance.preRegUserData.name = ""
            textView.text = LoggedUserRepository.instance.preRegUserData.toString()

        }


    }
}