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
        }
    }
}