package com.undef.fitapp.ui.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel : ViewModel() {
    /* test
    fun changeConsumedText() {
        _consumedText.apply { value = "asd" }
    }*/

    val _consumedText = MutableLiveData<String>().apply {
        value = "Consumed: 0 kcals";
    }
    private val _burnedText = MutableLiveData<String>().apply {
        value = "Burned: 0 kcals";
    }
    private val _remainingText = MutableLiveData<String>().apply {
        value = "Remaining: 0 kcals";
    }

    val consumedText: LiveData<String> = _consumedText;
    val burnedText: LiveData<String> = _burnedText;
    val remainingText: LiveData<String> = _remainingText;

}