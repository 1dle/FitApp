package com.undef.fitapp.ui.createprofile

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.undef.fitapp.R
import com.undef.fitapp.ui.main.SectionsPagerAdapter
import com.undef.fitapp.ui.createprofile.fragment.GoalFragment
import com.undef.fitapp.ui.createprofile.fragment.LoginDataFragment
import com.undef.fitapp.ui.createprofile.fragment.MetadataFragment


class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        //val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val sectionsPagerAdapter = object: FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private val TAB_TITLES = arrayOf(
                R.string.tab1Title,
                R.string.tab2Title,
                R.string.tab3Title
            )

            override fun getItem(position: Int): Fragment {
                return when(position){
                    0 -> LoginDataFragment()
                    1 -> MetadataFragment()
                    else -> GoalFragment()
                }
            }

            override fun getCount(): Int {
                return TAB_TITLES.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return resources.getString(TAB_TITLES[position])
            }
        }
        val viewPager: ViewPager = findViewById(R.id.viewPagerCP)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)


        val tabStrip = tabs.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener { v, event -> true }
        }


        //val fab: FloatingActionButton = findViewById(R.id.fab)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
    }
}