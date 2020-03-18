package com.undef.fitapp.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditMealViewModel: ViewModel() {
    private val _mealName = MutableLiveData<String>().apply {
        value = "meal.name"
    }
    private val _mealCalories = MutableLiveData<String>().apply {
        value = "meal.calories"
    }
    private val _mealProtein = MutableLiveData<String>().apply {
        value = "meal.protein"
    }
    private val _mealNetCarbs = MutableLiveData<String>().apply {
        value = "meal.netcarbs"
    }
    private val _mealFat = MutableLiveData<String>().apply {
        value = "meal.fat"
    }
    val mealName: LiveData<String> = _mealName
    val mealCalories: LiveData<String> = _mealCalories
    val mealProtein: LiveData<String> = _mealProtein
    val mealNetCarbs: LiveData<String> = _mealNetCarbs
    val mealFat: LiveData<String> = _mealFat

}