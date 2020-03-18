package com.undef.fitapp.ui.diary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.undef.fitapp.R

class EditMealActivity : AppCompatActivity() {


    private lateinit var editMealViewModel: EditMealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meal)

        editMealViewModel = ViewModelProviders.of(this).get(EditMealViewModel::class.java)

        val liveDailyStats = mutableListOf<Pair<TextView, LiveData<String>>>(
            // Pair ( UI element name , property name in ViewModel )
            Pair(findViewById(R.id.tvEditMealName), editMealViewModel.mealName),
            Pair(findViewById(R.id.tvEditMealCalories), editMealViewModel.mealCalories),
            Pair(findViewById(R.id.tvEditMealProtein), editMealViewModel.mealProtein),
            Pair(findViewById(R.id.tvEditMealNetCarbs), editMealViewModel.mealNetCarbs),
            Pair(findViewById(R.id.tvEditMealFat), editMealViewModel.mealFat)
        )

        liveDailyStats.forEach { pit ->
            pit.second.observe(this, Observer {
                pit.first.text = it
            })
        }

    }
}
