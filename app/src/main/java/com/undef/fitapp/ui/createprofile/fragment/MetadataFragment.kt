package com.undef.fitapp.ui.createprofile.fragment

//import android.support.v4.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R
import com.undef.fitapp.api.repositories.UserDataRepository
import kotlinx.android.synthetic.main.fragment_meta_data.*
import java.util.*


class MetadataFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meta_data, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mViewPager : ViewPager = activity!!.findViewById(R.id.viewPagerCP)

        val btnNext : Button = view.findViewById(R.id.btnMDNext)

        //fill spinners
        spBirthDateYear.apply {
            adapter = ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, (1920..2020).toList())
            setSelection(adapter.count/2)
        }
        spBirthDateMonth.apply {
            adapter = ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, (1..12).toList())
            setSelection(0)
        }
        spBirthDateDay.apply {
            //februárban is 31 nap van :( todo: fix
            adapter = ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, (1..31).toList())
            setSelection(0)
        }


        btnNext.setOnClickListener{

            UserDataRepository.preRegUserData.male = spSex.selectedItem.toString().toLowerCase(Locale.ROOT)
            UserDataRepository.preRegUserData.birthDate = "%d-%02d-%02d".format(spBirthDateYear.selectedItem,spBirthDateMonth.selectedItem, spBirthDateDay.selectedItem)

            if(!etRegWeight.text.isNullOrEmpty() && !etRegWeight.text.isNullOrEmpty() && !etRegName.text.isNullOrEmpty()){
                UserDataRepository.preRegUserData.name = etRegName.text.toString()
                UserDataRepository.preRegUserData.weight = etRegWeight.text.toString().toDouble()
                UserDataRepository.preRegUserData.height = etRegHeight.text.toString().toDouble()

                mViewPager.arrowScroll(View.FOCUS_RIGHT)
            }else{
                Toast.makeText(context, getString(R.string.fill_all_input), Toast.LENGTH_SHORT).show()
            }


        }
    }
}