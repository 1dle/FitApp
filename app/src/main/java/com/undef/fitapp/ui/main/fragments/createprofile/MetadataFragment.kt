package com.undef.fitapp.ui.main.fragments.createprofile

//import android.support.v4.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.undef.fitapp.R


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

        btnNext.setOnClickListener{
            mViewPager.arrowScroll(View.FOCUS_RIGHT)
        }

        //fill spinner with numbers
        val mspin : Spinner = view.findViewById(R.id.spinnerBirthDate)
        val items = (1920..2020).toList().toTypedArray()
        val adapter: ArrayAdapter<Int> =
            ArrayAdapter<Int>(activity!!, android.R.layout.simple_spinner_item, items)
        mspin.adapter = adapter
        mspin.setSelection(items.size/2)
    }
}