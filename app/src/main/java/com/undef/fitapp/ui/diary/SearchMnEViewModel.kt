package com.undef.fitapp.ui.diary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchMnEViewModel: ViewModel() {
    private val _consumedText = MutableLiveData<List<String>>().apply {
        value = mutableListOf("asd", "asd")
    }
}