package com.undef.fitapp.ui.main.fragments.createprofile

import android.os.Bundle
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R
import com.undef.fitapp.repositories.LoggedUserRepository
import kotlinx.android.synthetic.main.fragment_login_data.*
import kotlinx.android.synthetic.main.fragment_login_data.view.*

class LoginDataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_data, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewPager : ViewPager = activity!!.findViewById(R.id.viewPagerCP)

        val btnNext : Button = view.findViewById(R.id.btnCLDNext)

        btnNext.setOnClickListener{
            mViewPager.arrowScroll(View.FOCUS_RIGHT)

            //TODO: Warning if the next inputs are empty

            //TODO: Check if empty, or incorrect
            LoggedUserRepository.instance.preRegUserData.email = etRegEmail.text.toString()

            //TODO: Check if empty
            LoggedUserRepository.instance.preRegUserData.password = etRegPassword.text.toString()


        }
    }
}
