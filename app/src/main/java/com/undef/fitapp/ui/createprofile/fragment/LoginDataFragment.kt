package com.undef.fitapp.ui.createprofile.fragment

import android.os.Bundle
//import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R
import com.undef.fitapp.api.repositories.UserDataRepository
import kotlinx.android.synthetic.main.fragment_login_data.*

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
            if(!etRegEmail.text.isNullOrEmpty() && !etRegPassword.text.isNullOrEmpty()){
                UserDataRepository.preRegUserData.email = etRegEmail.text.toString()
                UserDataRepository.preRegUserData.password = etRegPassword.text.toString()

                mViewPager.arrowScroll(View.FOCUS_RIGHT)
            }else{
                Toast.makeText(context, getString(R.string.fill_all_input), Toast.LENGTH_SHORT).show()
            }


        }
    }
}
