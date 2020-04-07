package com.undef.fitapp.ui.createprofile.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R
import com.undef.fitapp.api.model.UserData
import com.undef.fitapp.api.repositories.MyCalendar
import com.undef.fitapp.api.repositories.MyCalendar.dateToString
import com.undef.fitapp.api.repositories.MyCalendar.getCurrentDate
import com.undef.fitapp.api.repositories.MyCalendar.today
import com.undef.fitapp.api.repositories.UserDataRepository
import com.undef.fitapp.api.service.ConnectionData
import com.undef.fitapp.ui.diary.HomeActivity
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse


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
            if(rgGoal.checkedRadioButtonId != -1){
                UserDataRepository.preRegUserData.registerDate = dateToString(getCurrentDate())

                CoroutineScope(Dispatchers.IO).launch{
                    val response = ConnectionData.service.registerUser(UserDataRepository.preRegUserData).awaitResponse()
                    if(response.isSuccessful){
                        if(response.body()!=null && response.body()!! > 1){
                            //register successful and response contains the regustered users id
                            UserDataRepository.loggedUser = UserDataRepository.preRegUserData.copy(id = response.body()!!)

                            withContext(Dispatchers.Main){
                                //launch Home activity
                                val intent = Intent(activity, HomeActivity::class.java)
                                startActivity(intent)

                            }

                        }else{
                            //todo :ha nem sikerül a reg
                        }
                    }
                }

            }else{
                Toast.makeText(context, "Select a goal!", Toast.LENGTH_SHORT).show()
            }


        }


    }
}