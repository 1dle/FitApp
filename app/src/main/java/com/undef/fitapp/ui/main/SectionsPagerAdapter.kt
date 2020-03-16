package com.undef.fitapp.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.undef.fitapp.R
import com.undef.fitapp.ui.main.fragments.createprofile.GoalFragment
import com.undef.fitapp.ui.main.fragments.createprofile.LoginDataFragment
import com.undef.fitapp.ui.main.fragments.createprofile.MetadataFragment

private val TAB_TITLES = arrayOf(
    "Bejelentkezési adatok",
    "Személyes adatok megadása",
    "Cél megadása"
)
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        //return PlaceholderFragment.newInstance(position + 1)
        return when(position){
            0 -> LoginDataFragment()
            1 -> MetadataFragment()
            else -> GoalFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return context.resources.getString(TAB_TITLES[position])
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 3
    }
}